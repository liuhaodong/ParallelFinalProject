package edu.cmu.cs15618.finalproject.datatype;

import java.io.Serializable;

public class WorkerStatus implements Serializable {
	private long freeMem;
	private double cpuPerc;

	public WorkerStatus(long pFreeMem, double pCpuPerc) {
		this.freeMem = pFreeMem;
		this.cpuPerc = pCpuPerc;
	}

	public long getFreeMem() {
		return freeMem;
	}

	public void setFreeMem(int freeMem) {
		this.freeMem = freeMem;
	}

	public double getCpuPerc() {
		return cpuPerc;
	}

	public void setCpuPerc(double cpuPerc) {
		this.cpuPerc = cpuPerc;
	}

	@Override
	public String toString() {
		return "CPU Usage: " + this.cpuPerc + " Free Mem: " + freeMem;
	}

}
