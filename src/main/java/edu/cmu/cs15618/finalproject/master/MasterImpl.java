package edu.cmu.cs15618.finalproject.master;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.cmu.cs15618.finalproject.classifier.REPTreePredictor;
import edu.cmu.cs15618.finalproject.classifier.RequestNumPredictor;
import edu.cmu.cs15618.finalproject.config.ServerConfigurations;
import edu.cmu.cs15618.finalproject.datatype.MessageType;
import edu.cmu.cs15618.finalproject.datatype.RequestMessage;
import edu.cmu.cs15618.finalproject.datatype.ResponseMessage;
import edu.cmu.cs15618.finalproject.datatype.ServerAddress;
import edu.cmu.cs15618.finalproject.datatype.WorkerStatus;
import edu.cmu.cs15618.finalproject.harness.Client;
import edu.cmu.cs15618.finalproject.worker.Worker;

public class MasterImpl implements Master {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2596170267975352299L;

	private List<ServerAddress> avaialbeWorkerAddresses;

	private List<ServerAddress> daemonAddresses;

	private ServerSocket serverSocket;

	private ExecutorService executors;

	private RequestNumPredictor requestNumPredictor;

	public MasterImpl() {
		requestNumPredictor = new REPTreePredictor();

		executors = Executors.newFixedThreadPool(800);
		avaialbeWorkerAddresses = new ArrayList<ServerAddress>();
		daemonAddresses = new ArrayList<ServerAddress>();
		try {
			this.serverSocket = new ServerSocket(
					ServerConfigurations.MASTER_DEFAULT_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MasterImpl(int port) {
		requestNumPredictor = new REPTreePredictor();
		executors = Executors.newFixedThreadPool(800);
		avaialbeWorkerAddresses = new ArrayList<ServerAddress>();
		daemonAddresses = new ArrayList<ServerAddress>();
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initMaster() {
		try {
			// Test Trace
			requestNumPredictor
					.initClassifier("data/NASA_access_log_Aug95.arff");

			BufferedReader br = new BufferedReader(new FileReader(
					"worker_addresses"));
			String line;

			while ((line = br.readLine()) != null) {
				String[] tmp = line.split(" ");
				String ip = tmp[0];
				int port = Integer.parseInt(tmp[1]);
				daemonAddresses.add(new ServerAddress(ip, port));
				bootWorker(new ServerAddress(ip, port));
			}

			br.close();

			new Timer().schedule(new MasterTickTask(), 1000, 1000);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The tick task has 2 things to do: 1. Update worker status 2. Update
	 * classifier
	 * 
	 * @author haodongl
	 *
	 */
	private class MasterTickTask extends TimerTask {

		@Override
		public void run() {
			for (ServerAddress addr : daemonAddresses) {
				try {
					WorkerStatus status = getWorkerStatus(addr);
					System.out.println(status.toString());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private WorkerStatus getWorkerStatus(ServerAddress daemonAddress)
				throws UnknownHostException, IOException,
				ClassNotFoundException {

			Socket socket = new Socket(daemonAddress.getIP(),
					daemonAddress.getPort());

			ObjectOutputStream objOut = new ObjectOutputStream(
					socket.getOutputStream());
			objOut.writeObject(new RequestMessage(MessageType.GET_STATUS, ""));

			ObjectInputStream in = new ObjectInputStream(
					socket.getInputStream());

			WorkerStatus status = (WorkerStatus) in.readObject();

			return status;
		}

	}

	private void startListenClientRequest() {
		while (true) {

			try {
				Socket socket = this.serverSocket.accept();
				if (this.avaialbeWorkerAddresses.isEmpty()) {
					socket.close();
					continue;
				}

				// System.out.println("new request");

				ObjectInputStream objIn = new ObjectInputStream(
						socket.getInputStream());

				RequestMessage request = (RequestMessage) objIn.readObject();

				ServerAddress workerAddress = this.avaialbeWorkerAddresses
						.get(0);

				executors.execute(new RequestDispatcher(socket, request,
						workerAddress));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class RequestDispatcher implements Runnable {

		private Socket socket;
		private RequestMessage request;
		private ServerAddress workerAddress;

		public RequestDispatcher(Socket pSocket,
				RequestMessage pRequestMessage, ServerAddress pWorkerAddress) {
			this.socket = pSocket;
			this.request = pRequestMessage;
			this.workerAddress = pWorkerAddress;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {

				Socket workerSocket = new Socket(workerAddress.getIP(),
						workerAddress.getPort());

				ObjectOutputStream objOut = new ObjectOutputStream(
						workerSocket.getOutputStream());
				// System.out.println(workerSocket.getPort());
				objOut.writeObject(this.request);
				objOut.flush();
				// System.out.println("start dispatch");
				ObjectInputStream objInput = new ObjectInputStream(
						workerSocket.getInputStream());
				ResponseMessage response = (ResponseMessage) objInput
						.readObject();

				ObjectOutputStream resultOut = new ObjectOutputStream(
						socket.getOutputStream());
				resultOut.writeObject(response);
				objInput.close();
				objOut.close();
				resultOut.close();
				workerSocket.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		initMaster();

		this.startListenClientRequest();
	}

	@Override
	public boolean bootWorker(ServerAddress daemonAddress) {
		try {
			System.out.println(daemonAddress.getIP() + daemonAddress.getPort());
			Socket socket = new Socket(daemonAddress.getIP(),
					daemonAddress.getPort());

			ObjectOutputStream objOut = new ObjectOutputStream(
					socket.getOutputStream());

			objOut.writeObject(new RequestMessage(MessageType.BOOT_WORKER, ""));

			ObjectInputStream objIn = new ObjectInputStream(
					socket.getInputStream());

			ServerAddress workerAddress = (ServerAddress) objIn.readObject();

			if (workerAddress instanceof ServerAddress) {
				this.avaialbeWorkerAddresses.add(new ServerAddress(
						daemonAddress.getIP(), workerAddress.getPort()));
				return true;
			}
			return false;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean killWorker(Worker worker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void handleClientRequest(Client client, RequestMessage request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWorkerResponse(Worker worker, ResponseMessage response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWorkerOnline(ServerAddress workerAddress) {
		this.avaialbeWorkerAddresses.add(workerAddress);
	}

	@Override
	public String getIP() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

}
