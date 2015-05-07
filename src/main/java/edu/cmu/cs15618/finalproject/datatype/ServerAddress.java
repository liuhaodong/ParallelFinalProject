package edu.cmu.cs15618.finalproject.datatype;

public class ServerAddress implements MachineInfo {

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
