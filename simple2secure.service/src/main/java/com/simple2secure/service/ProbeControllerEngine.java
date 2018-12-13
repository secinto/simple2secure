package com.simple2secure.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.service.interfaces.Engine;
import com.simple2secure.service.tasks.PortalWatchdog;
import com.simple2secure.service.tasks.ProbeMonitor;
import com.simple2secure.service.tasks.ProbeUpdater;

public class ProbeControllerEngine implements Engine {
	private static Logger log = LoggerFactory.getLogger(ProbeControllerEngine.class);

	private static final String VERSION = "0.1.0";
	private static final String NAME = "ProbeController Service";
	private static final String COMPLETE_NAME = NAME + ":" + VERSION;

	private ScheduledThreadPoolExecutor scheduler;

	private ScheduledFuture<?> probeMonitor;
	private ScheduledFuture<?> probeUpdater;
	private ScheduledFuture<?> portalWatchdog;

	public ProbeControllerEngine() {
		int processors = Runtime.getRuntime().availableProcessors();

		if (processors > 4) {
			processors = processors / 2;
		}

		scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(processors);
		scheduler.setRemoveOnCancelPolicy(true);
		scheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
		scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

	}

	@Override
	public boolean isStopped() {
		if (scheduler != null && scheduler.getActiveCount() == 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean start() {
		try {
			probeMonitor = scheduler.scheduleAtFixedRate(new ProbeMonitor(), 0, 1000, TimeUnit.MILLISECONDS);
			probeUpdater = scheduler.scheduleAtFixedRate(new ProbeUpdater(), 100, 1000, TimeUnit.MILLISECONDS);
			portalWatchdog = scheduler.scheduleAtFixedRate(new PortalWatchdog(), 200, 1000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			log.error("Couldn't start probe controller engine. Reason {}", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean stop() {
		scheduler.shutdown();
		return true;
	}

	@Override
	public String getName() {
		return COMPLETE_NAME;
	}
}
