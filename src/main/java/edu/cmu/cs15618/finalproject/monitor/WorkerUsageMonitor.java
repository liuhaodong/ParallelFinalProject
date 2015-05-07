package edu.cmu.cs15618.finalproject.monitor;

import java.lang.management.ManagementFactory;
import java.util.Random;

import com.sun.management.OperatingSystemMXBean;

import edu.cmu.cs15618.finalproject.datatype.WorkerStatus;

public class WorkerUsageMonitor implements WorkerMonitor {

	private OperatingSystemMXBean osBean;
	private Runtime runtime;

	public WorkerUsageMonitor() {
		osBean = ManagementFactory
				.getPlatformMXBean(OperatingSystemMXBean.class);
		runtime = Runtime.getRuntime();
	}

	@Override
	public WorkerStatus getWorkerStatus() {
		double jvmCPUPerc = osBean.getSystemCpuLoad();
		long jvmFreeMem = runtime.freeMemory();
		WorkerStatus currentStatus = new WorkerStatus(jvmFreeMem, jvmCPUPerc);
		return currentStatus;
	}
}
