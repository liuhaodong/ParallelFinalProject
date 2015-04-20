package edu.cmu.cs15618.finalproject.datatype;

import edu.cmu.cs15618.finalproject.MachineInfo;

public interface Message {

	MessageType getMessageType();

	void setMessageType();

	String getContent();

	void setContent();

	MachineInfo getSourceMachineInfo();

	MachineInfo getDestMachineInfo();
}
