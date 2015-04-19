package edu.cmu.cs15618.finalproject.worker;

import edu.cmu.cs15618.finalproject.datatype.RequestMessage;
import edu.cmu.cs15618.finalproject.master.Master;


public interface Worker {
	void processRequest(Master master, RequestMessage request);
}
