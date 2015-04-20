package edu.cmu.cs15618.finalproject.harness;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.cmu.cs15618.finalproject.datatype.MessageType;
import edu.cmu.cs15618.finalproject.datatype.RequestMessage;
import edu.cmu.cs15618.finalproject.datatype.ResponseMessage;

public class ClientImpl implements Client, Runnable {

	private Socket socket;

	public ClientImpl(String pMasterIP, int pMasterPort) {
		try {
			socket = new Socket(pMasterIP, pMasterPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ObjectInputStream objIn = null;
		ObjectOutputStream objOut = null;

		try {
			objIn = new ObjectInputStream(socket.getInputStream());
			objOut = new ObjectOutputStream(socket.getOutputStream());
			
			while (true) {
				objOut.writeObject(new RequestMessage(MessageType.WORK, ""));
				System.out.println("Master get request");
				ResponseMessage response = (ResponseMessage) objIn.readObject();
				if (response.getMessageType() == MessageType.ACTION_SUCCESS) {
					System.out.println("Get Response");
				}
				
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
