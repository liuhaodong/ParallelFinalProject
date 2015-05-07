package edu.cmu.cs15618.finalproject;

import edu.cmu.cs15618.finalproject.harness.Client;
import edu.cmu.cs15618.finalproject.harness.ClientImpl;
import edu.cmu.cs15618.finalproject.master.Master;
import edu.cmu.cs15618.finalproject.master.MasterImpl;
import edu.cmu.cs15618.finalproject.worker.WorkerDaemon;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if (args.length < 1) {
			System.out.println("Usage: client/master/worker port");
		}

		String type = args[0];
		int port = Integer.parseInt(args[1]);

		if (type.equals("client")) {
			Client client = new ClientImpl();
			new Thread(client).start();
		} else if (type.equals("master")) {
			Master master = new MasterImpl(port);
			new Thread(master).start();
		} else if (type.equals("worker")) {
			WorkerDaemon workerDaemon = new WorkerDaemon(port);
			new Thread(workerDaemon).start();
		}

//		WorkerDaemon testDaemon = new WorkerDaemon();
//		new Thread(testDaemon).start();
//		MasterImpl testMasterImpl = new MasterImpl();
//		new Thread(testMasterImpl).start();
//		try {
//			Thread.sleep(1500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ClientImpl testClientImpl = new ClientImpl("localhost", 3000);
//		new Thread(testClientImpl).start();
	}
}
