package edu.cmu.cs15618.finalproject.harness;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.cmu.cs15618.finalproject.datatype.MessageType;
import edu.cmu.cs15618.finalproject.datatype.RequestMessage;
import edu.cmu.cs15618.finalproject.datatype.ResponseMessage;

public class ClientImpl implements Client {

	private String masterIP;
	private int masterPort;
	private Socket socket;

	public ClientImpl() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"master_addresses"));

			String line = br.readLine();
			String masterInfo[] = line.split(" ");
			masterIP = masterInfo[0];
			masterPort = Integer.parseInt(masterInfo[1]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ClientImpl(String pMasterIP, int pMasterPort) {
		this.masterIP = pMasterIP;
		this.masterPort = pMasterPort;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ObjectInputStream objIn = null;
		ObjectOutputStream objOut = null;
		System.out.println("client send request");
		try {
			while (true) {
				socket = new Socket(this.masterIP, this.masterPort);
				objOut = new ObjectOutputStream(socket.getOutputStream());

				objOut.writeObject(new RequestMessage(MessageType.WORK, ""));

				objIn = new ObjectInputStream(socket.getInputStream());
				ResponseMessage response = (ResponseMessage) objIn.readObject();
				if (response.getMessageType() == MessageType.ACTION_SUCCESS) {
					System.out.println("Get Response");
				}
				Thread.sleep(500);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
