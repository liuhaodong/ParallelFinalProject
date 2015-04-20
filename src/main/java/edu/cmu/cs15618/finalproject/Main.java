package edu.cmu.cs15618.finalproject;

import edu.cmu.cs15618.finalproject.harness.ClientImpl;
import edu.cmu.cs15618.finalproject.master.MasterImpl;
import edu.cmu.cs15618.finalproject.worker.WorkerDaemon;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WorkerDaemon testDaemon = new WorkerDaemon();
		new Thread(testDaemon).start();
		MasterImpl testMasterImpl = new MasterImpl();
		new Thread(testMasterImpl).start();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ClientImpl testClientImpl = new ClientImpl("localhost", 3000);
		new Thread(testClientImpl).start();
		
	}

}
