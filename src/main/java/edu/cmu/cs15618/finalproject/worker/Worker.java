package edu.cmu.cs15618.finalproject.worker;

import java.net.Socket;

import edu.cmu.cs15618.finalproject.datatype.RequestMessage;

public interface Worker {
	void processRequest(Socket socket, RequestMessage request);
}
