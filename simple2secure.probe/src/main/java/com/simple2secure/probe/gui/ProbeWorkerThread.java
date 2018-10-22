package com.simple2secure.probe.gui;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.network.NetworkMonitor;
import com.simple2secure.probe.scheduler.ConfigScheduler;
import com.simple2secure.probe.scheduler.NetworkScheduler;
import com.simple2secure.probe.scheduler.QueryScheduler;
import com.simple2secure.probe.scheduler.ReportScheduler;

public class ProbeWorkerThread extends Thread {

	private static Logger log = LoggerFactory.getLogger(ProbeWorkerThread.class);

	final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
	
	static NetworkMonitor networkMonitor;
	
	static Timer time;

	@Override
	public void run() {
		log.debug("ProbeWorkerThread running");

		networkMonitor = NetworkMonitor.startMonitor();
		
		time = new Timer();
		
		time.schedule(new ConfigScheduler(), 0, TimeUnit.MINUTES.toMillis(1));
		time.schedule(new ReportScheduler(), 0, TimeUnit.MINUTES.toMillis(1));
		time.schedule(new NetworkScheduler(networkMonitor), 0, TimeUnit.MINUTES.toMillis(1));
		time.schedule(new QueryScheduler(), 0, TimeUnit.MINUTES.toMillis(1));
		
	}
	
	public static void stopTimerTasks() {
		networkMonitor.stop();
		time.cancel();
	}
}
