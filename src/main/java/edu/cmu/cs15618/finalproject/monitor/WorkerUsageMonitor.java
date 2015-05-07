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

	public static void main(String[] args) {
		OperatingSystemMXBean osBean = ManagementFactory
				.getPlatformMXBean(OperatingSystemMXBean.class);
		// What % CPU load this current JVM is taking, from 0.0-1.0
		System.out.println(osBean.getProcessCpuLoad());

		int mb = 1024 * 1024;

		// Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();

		System.out.println("##### Heap utilization statistics [MB] #####");

		// Print used memory
		System.out.println("Used Memory:"
				+ (runtime.totalMemory() - runtime.freeMemory()) / mb);

		// Print free memory
		System.out.println("Free Memory:" + runtime.freeMemory() / mb);

		// Print total available memory
		System.out.println("Total Memory:" + runtime.totalMemory() / mb);

		// Print Maximum available memory
		System.out.println("Max Memory:" + runtime.maxMemory() / mb);

		// What % load the overall system is at, from 0.0-1.0
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int j = 0; j < 100; j++) {
					for (int i = 0; i < 1000 * 10000; i++) {
						int b = new Random().nextInt();
					}
					System.out.println(osBean.getTotalPhysicalMemorySize());
					System.out.println(osBean.getSystemCpuLoad());
				}

			}
		}).start();

	}
}
