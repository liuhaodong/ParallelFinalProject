package edu.cmu.cs15618.finalproject.master;

import edu.cmu.cs15618.finalproject.datatype.RequestMessage;
import edu.cmu.cs15618.finalproject.datatype.ResponseMessage;
import edu.cmu.cs15618.finalproject.harness.Client;
import edu.cmu.cs15618.finalproject.worker.Worker;

public class MasterImpl implements Master, Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean bootWorker(Worker worker) {
		// TODO Auto-generated method stub
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
	public void handleWorkerOnline(Worker worker) {
		// TODO Auto-generated method stub
		
	}

}
