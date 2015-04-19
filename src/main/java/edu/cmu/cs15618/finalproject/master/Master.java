package edu.cmu.cs15618.finalproject.master;

import edu.cmu.cs15618.finalproject.datatype.RequestMessage;
import edu.cmu.cs15618.finalproject.datatype.ResponseMessage;
import edu.cmu.cs15618.finalproject.harness.Client;
import edu.cmu.cs15618.finalproject.worker.Worker;

public interface Master {
	boolean bootWorker(Worker worker);
	boolean killWorker(Worker worker);
	void handleWorkerOnline(Worker worker);
	void handleClientRequest(Client client, RequestMessage request);
	void handleWorkerResponse(Worker worker, ResponseMessage response);
}
