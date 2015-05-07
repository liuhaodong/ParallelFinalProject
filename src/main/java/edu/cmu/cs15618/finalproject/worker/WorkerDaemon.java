package edu.cmu.cs15618.finalproject.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.cmu.cs15618.finalproject.config.ServerConfigurations;
import edu.cmu.cs15618.finalproject.datatype.MachineInfo;
import edu.cmu.cs15618.finalproject.datatype.MessageType;
import edu.cmu.cs15618.finalproject.datatype.RequestMessage;
import edu.cmu.cs15618.finalproject.datatype.ServerAddress;
import edu.cmu.cs15618.finalproject.datatype.WorkerStatus;
import edu.cmu.cs15618.finalproject.master.Master;

public class WorkerDaemon implements Runnable, MachineInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3567000012557169504L;
	private ServerSocket daemonSocket;

	public WorkerDaemon() {
		try {
			daemonSocket = new ServerSocket(
					ServerConfigurations.DAEMON_DEFAULT_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public WorkerDaemon(int port) {
		try {
			daemonSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void handleBootWorkerRequest(Socket masterSocket) {
		try {
			OutputStream out = masterSocket.getOutputStream();

			ObjectOutputStream objOut = new ObjectOutputStream(out);

			ServerAddress newWorkerAddress = this.bootWorker();

			objOut.writeObject(newWorkerAddress);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ServerAddress bootWorker() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WorkerImpl newWorker = new WorkerImpl();
		new Thread(newWorker).start();
		return new ServerAddress(newWorker.getIP(), newWorker.getPort());
	}

	public void handleKillWorkerRequest(Master master) {

	}

	private void killWorker() {

	}

	public WorkerStatus getCurrentStatus() {
		return null;
		// TODO
	}

	@Override
	public void run() {
		while (true) {
			try {

				Socket masterRequestSocket = this.daemonSocket.accept();
				System.out.println("hi");
				ObjectInputStream in = new ObjectInputStream(
						masterRequestSocket.getInputStream());
				RequestMessage requestMessage = (RequestMessage) in
						.readObject();

				MessageType type = requestMessage.getMessageType();
				if (type == MessageType.BOOT_WORKER) {
					this.handleBootWorkerRequest(masterRequestSocket);

				} else if (type == MessageType.KILL_WORKER) {

				} else if (type == MessageType.GET_STATUS) {

				}

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
