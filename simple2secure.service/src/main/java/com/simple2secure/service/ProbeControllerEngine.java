package com.simple2secure.service;

import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.commons.process.ProcessUtils;
import com.simple2secure.service.interfaces.Engine;
import com.simple2secure.service.observer.SimpleLoggingObserver;
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

	private ProcessContainer probeProcess;
	private SimpleLoggingObserver observer;
	private int exitValue = 0;

	public ProbeControllerEngine() {
		int processors = Runtime.getRuntime().availableProcessors();

		if (processors > 4) {
			processors = processors / 2;
		}

		scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(processors);
		scheduler.setRemoveOnCancelPolicy(true);
		scheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
		scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

		observer = new SimpleLoggingObserver();

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
		log.debug("Starting {}", this.getName());
		if (startProbe()) {
			try {
				log.debug("Creating ProbeMonitor scheduled task");
				probeMonitor = scheduler.scheduleAtFixedRate(new ProbeMonitor(), 0, 10000, TimeUnit.MILLISECONDS);
				log.debug("Creating ProbeUpdater scheduled task");
				probeUpdater = scheduler.scheduleAtFixedRate(new ProbeUpdater(), 100, 10000, TimeUnit.MILLISECONDS);
				log.debug("Creating PortalWatchdog scheduled task");
				portalWatchdog = scheduler.scheduleAtFixedRate(new PortalWatchdog(), 200, 10000, TimeUnit.MILLISECONDS);
				return true;
			} catch (Exception e) {
				log.error("Couldn't start probe controller engine. Reason {}", e);
			}
		}
		return false;
	}

	public ScheduledFuture<?> getProbeMonitor() {
		return probeMonitor;
	}

	public void setProbeMonitor(ScheduledFuture<?> probeMonitor) {
		this.probeMonitor = probeMonitor;
	}

	public ScheduledFuture<?> getProbeUpdater() {
		return probeUpdater;
	}

	public void setProbeUpdater(ScheduledFuture<?> probeUpdater) {
		this.probeUpdater = probeUpdater;
	}

	public ScheduledFuture<?> getPortalWatchdog() {
		return portalWatchdog;
	}

	public void setPortalWatchdog(ScheduledFuture<?> portalWatchdog) {
		this.portalWatchdog = portalWatchdog;
	}

	private boolean startProbe() {
		try {
			probeProcess = ProcessUtils.invokeJavaProcess("-jar", "simple2secure.probe-0.1.0.jar", "-l", "license.zip");
			log.debug("Probe started using JAR. Alive {}", probeProcess.getProcess().isAlive());
			probeProcess.getObservable().addObserver(observer);
			probeProcess.startObserving();
			log.debug("Added output observer to probe process.");
			return true;
		} catch (FileNotFoundException | InterruptedException ie) {
			log.error("Couldn't invoke Probe using standard parameters. Reason {}", ie);
		}
		return false;
	}

	@Override
	public boolean stop() {
		if (probeProcess != null) {
			try {
				probeProcess.getProcess().destroy();
				log.debug("Destroyed Probe process. Exit value was {}", exitValue);
				if (!probeProcess.getProcess().isAlive()) {
					exitValue = probeProcess.getProcess().exitValue();
					log.debug("Exit value was {}", exitValue);
				}
			} catch (Exception e) {
				log.error("Stopping didn't work correctly! Reason {}", e);
			}
		}
		try {
			scheduler.shutdown();
		} catch (Exception e) {
			log.error("Shuting down scheduler didn't work. Reason {}", e);
		}
		return true;
	}

	@Override
	public String getName() {
		return COMPLETE_NAME;
	}
}
