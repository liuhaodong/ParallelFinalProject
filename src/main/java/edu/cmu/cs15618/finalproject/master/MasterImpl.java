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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private volatile Map<ServerAddress, ServerAddress> daemonToWorkerAddressMap;

	private volatile Map<ServerAddress, WorkerStatus> workerStatusMap;

	private ServerSocket serverSocket;

	private ExecutorService executors;

	private RequestNumPredictor requestNumPredictor;

	private volatile int timer;

	private Map<ServerAddress, Long> workerLatency;

	private long avgMasterLatency;
	private long avgWorkerLatency;

	private volatile int clientRequestCounter = 0;
	private volatile int processedRequestCounter = 0;

	public MasterImpl() {
		requestNumPredictor = new REPTreePredictor();
		executors = Executors.newCachedThreadPool();
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
		daemonToWorkerAddressMap = new HashMap<ServerAddress, ServerAddress>();
		workerStatusMap = new HashMap<ServerAddress, WorkerStatus>();

		requestNumPredictor = new REPTreePredictor();
		executors = Executors.newCachedThreadPool();
		avaialbeWorkerAddresses = new ArrayList<ServerAddress>();
		daemonAddresses = new ArrayList<ServerAddress>();
		try {
			this.serverSocket = new ServerSocket(port,1000);
		} catch (IOException e) {
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

			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					timer++;
				}
			}, 0, 1000);

			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					for (ServerAddress addr : daemonAddresses) {
						try {
							WorkerStatus status = getWorkerStatus(addr);
							ServerAddress workerAddr = daemonToWorkerAddressMap
									.get(addr);
							workerStatusMap.put(workerAddr, status);

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
			}, 0, 1000);

			new Timer().schedule(new MasterTickTask(), 0, 5000);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This timer task update classifier, also checks if worker need to
	 * boot/kill.
	 * 
	 * @author haodongl
	 *
	 */
	private class MasterTickTask extends TimerTask {

		@Override
		public void run() {

		}

	}

	// Get Predict request
	private int getPredictRequestNum() {
		int currentTime = this.timer;
		return this.requestNumPredictor.predictRequestNum(currentTime);
	}

	private WorkerStatus getWorkerStatus(ServerAddress daemonAddress)
			throws UnknownHostException, IOException, ClassNotFoundException {

		Socket socket = new Socket(daemonAddress.getIP(),
				daemonAddress.getPort());

		ObjectOutputStream objOut = new ObjectOutputStream(
				socket.getOutputStream());
		objOut.writeObject(new RequestMessage(MessageType.GET_STATUS, ""));

		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

		WorkerStatus status = (WorkerStatus) in.readObject();

		return status;
	}

	private void startListenClientRequest() {
		while (true) {
			try {
				Socket socket = this.serverSocket.accept();

				// Add up request counter
				clientRequestCounter++;

				if (this.avaialbeWorkerAddresses.isEmpty()) {
					socket.close();
					continue;
				}

				ObjectInputStream objIn = new ObjectInputStream(
						socket.getInputStream());

				RequestMessage request = (RequestMessage) objIn.readObject();

				String content = request.getContent();
				// System.out.println("request: " + content);
				int currentTime = Integer.parseInt(content.split(" ")[1]);
				timer = currentTime;

				ServerAddress workerAddress = this.getBestWorker();

				// ServerAddress workerAddress = avaialbeWorkerAddresses.get(0);

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

	private ServerAddress getBestWorker() {

		double bestAvailResource = 0;

		ServerAddress bestWorker = avaialbeWorkerAddresses.get(0);
		// System.out.println("availe workers:" +
		// avaialbeWorkerAddresses.size());
		for (ServerAddress worker : avaialbeWorkerAddresses) {
			// System.out.println(workerStatusMap.containsKey(worker));
			WorkerStatus status = this.workerStatusMap.get(worker);
			if (status == null) {
				continue;
			}
			// System.out.println(status.toString());
			// double availResource = (1 - status.getCpuPerc())
			// + (1 - (double) status.getFreeMem() / status.getTotalMem());
			double availResource = status.getFreeMem();
			if (availResource >= bestAvailResource) {
				bestWorker = worker;
				bestAvailResource = availResource;
			}
		}
		return bestWorker;
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
			try {
				Socket workerSocket = new Socket(workerAddress.getIP(),
						workerAddress.getPort());
//				Socket workerSocket = workerSocketMap.get(workerAddress);
				ObjectOutputStream objOut = new ObjectOutputStream(
						workerSocket.getOutputStream());
				objOut.writeObject(this.request);
				// objOut.flush();
				ObjectInputStream objInput = new ObjectInputStream(
						workerSocket.getInputStream());
				ResponseMessage response = (ResponseMessage) objInput
						.readObject();

				ObjectOutputStream resultOut = new ObjectOutputStream(
						socket.getOutputStream());
				resultOut.writeObject(response);

				// Successfully processed a request.
				processedRequestCounter++;

				objInput.close();
				objOut.close();
				resultOut.close();
				workerSocket.close();
				socket.close();

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
		initMaster();
		// Periodically collect master status
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				int oldRequest = processedRequestCounter;
				try {
					Thread.sleep(900);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int newRequest = processedRequestCounter;

				System.out.println("Current Throughput: "
						+ (newRequest - oldRequest) + " Current WorkerNum: "
						+ avaialbeWorkerAddresses.size());
			}
		}, 0, 100);
		this.startListenClientRequest();

	}

	@Override
	public boolean bootWorker(ServerAddress daemonAddress) {
		try {
			Socket socket = new Socket(daemonAddress.getIP(),
					daemonAddress.getPort());

			ObjectOutputStream objOut = new ObjectOutputStream(
					socket.getOutputStream());

			objOut.writeObject(new RequestMessage(MessageType.BOOT_WORKER, ""));

			ObjectInputStream objIn = new ObjectInputStream(
					socket.getInputStream());

			ServerAddress workerAddress = (ServerAddress) objIn.readObject();

			if (workerAddress instanceof ServerAddress) {
				ServerAddress workerAddr = new ServerAddress(
						daemonAddress.getIP(), workerAddress.getPort());
				this.avaialbeWorkerAddresses.add(workerAddr);

				this.daemonToWorkerAddressMap.put(daemonAddress, workerAddr);
				System.out.println(workerAddr.getIP() + workerAddr.getPort());
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
	public boolean killWorker(ServerAddress daemonAddress) {
		try {
			Socket socket = new Socket(daemonAddress.getIP(),
					daemonAddress.getPort());

			ObjectOutputStream objOut = new ObjectOutputStream(
					socket.getOutputStream());

			objOut.writeObject(new RequestMessage(MessageType.KILL_WORKER, ""));

			this.avaialbeWorkerAddresses.remove(daemonToWorkerAddressMap
					.get(daemonAddress));

			this.daemonToWorkerAddressMap.remove(daemonAddress);

			return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
