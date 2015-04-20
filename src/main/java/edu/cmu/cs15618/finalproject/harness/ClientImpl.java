package edu.cmu.cs15618.finalproject.harness;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.cmu.cs15618.finalproject.datatype.MessageType;
import edu.cmu.cs15618.finalproject.datatype.RequestMessage;
import edu.cmu.cs15618.finalproject.datatype.ResponseMessage;

public class ClientImpl implements Client, Runnable {
	
	private String masterIP;
	private int masterPort;
	private Socket socket;

	public ClientImpl(String pMasterIP, int pMasterPort) {
		this.masterIP = pMasterIP;
		this.masterPort = pMasterPort;
//		try {
////			socket = new Socket(pMasterIP, pMasterPort);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ObjectInputStream objIn = null;
		ObjectOutputStream objOut = null;
		System.out.println("client send request");
		try {
			// objIn = new ObjectInputStream(socket.getInputStream());
			

			while (true) {
				socket = new Socket(this.masterIP, this.masterPort);
				objOut = new ObjectOutputStream(socket.getOutputStream());
				
				objOut.writeObject(new RequestMessage(MessageType.WORK, ""));

				objIn = new ObjectInputStream(socket.getInputStream());
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
