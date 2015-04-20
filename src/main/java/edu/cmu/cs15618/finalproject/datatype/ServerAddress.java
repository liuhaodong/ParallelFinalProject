package edu.cmu.cs15618.finalproject.datatype;

import edu.cmu.cs15618.finalproject.MachineInfo;

public class ServerAddress implements MachineInfo {

	private String host;
	private int port;

	public ServerAddress(String host, int port) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getIP() {
		return this.host;
	}

	@Override
	public int getPort() {
		return this.port;
	}

}
