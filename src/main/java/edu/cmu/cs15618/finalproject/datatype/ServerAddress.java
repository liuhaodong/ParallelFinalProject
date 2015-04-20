package edu.cmu.cs15618.finalproject.datatype;

import java.io.Serializable;

import edu.cmu.cs15618.finalproject.MachineInfo;

public class ServerAddress implements MachineInfo, Serializable {

	private String host;
	private int port;

	public ServerAddress(String pHost, int pPort) {
		this.host = pHost;
		this.port = pPort;
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
