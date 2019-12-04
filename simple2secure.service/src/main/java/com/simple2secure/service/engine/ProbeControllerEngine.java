/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
package com.simple2secure.service.engine;

import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.commons.process.ProcessUtils;
import com.simple2secure.commons.service.ServiceCommand;
import com.simple2secure.commons.service.ServiceCommands;
import com.simple2secure.service.interfaces.ControllerEngine;
import com.simple2secure.service.model.TriggerableEngineStates;
import com.simple2secure.service.observer.SimpleLoggingObserver;
import com.simple2secure.service.tasks.PortalWatchdog;
import com.simple2secure.service.tasks.ProbeMonitor;
import com.simple2secure.service.tasks.ProbeUpdater;
import com.simple2secure.service.tasks.TriggerEngineState;

public class ProbeControllerEngine implements ControllerEngine {
	private static Logger log = LoggerFactory.getLogger(ProbeControllerEngine.class);

	private static final String VERSION = "0.1.0";
	private static final String NAME = "ProbeController";
	private static final String COMPLETE_NAME = NAME + ":" + VERSION;

	private ScheduledThreadPoolExecutor scheduler;
	private ScheduledThreadPoolExecutor triggerScheduler;

	private ScheduledFuture<?> probeMonitor;
	private ScheduledFuture<?> probeUpdater;
	private ScheduledFuture<?> portalWatchdog;

	private ProcessContainer probeProcess;
	private SimpleLoggingObserver observer;

	private String probeLibraryPath = "./libs/simple2secure.probe.jar";
	private String licensePath = System.getProperty("user.dir") + "/license/";

	private boolean stopped = true;

	private int exitValue = 0;
	private int processors = 1;

	public ProbeControllerEngine() {
		processors = Runtime.getRuntime().availableProcessors();

		if (processors > 4) {
			processors = processors / 2;
		}

		triggerScheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(processors);
		triggerScheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

		observer = new SimpleLoggingObserver();
		log.info("Using {} as licensePath", licensePath);
	}

	public ProbeControllerEngine(String probeLibraryPath, String licensePath) {
		this();
		this.probeLibraryPath = probeLibraryPath;
		this.licensePath = licensePath;
	}

	@Override
	public boolean isStopped() {
		return stopped;
	}

	@Override
	public ProcessContainer getControlledProcess() {
		return probeProcess;
	}

	@Override
	public boolean start() {
		log.debug("Starting {} in path {}", this.getName(), System.getProperty("user.dir"));

		initializeSchedulers();

		if (startProbe()) {
			try {
				log.debug("Creating ProbeMonitor scheduled task");
				probeMonitor = scheduler.scheduleAtFixedRate(new ProbeMonitor(this), 0, 10000, TimeUnit.MILLISECONDS);
				log.debug("Creating ProbeUpdater scheduled task");
				probeUpdater = scheduler.scheduleAtFixedRate(new ProbeUpdater(this), 100, 10000, TimeUnit.MILLISECONDS);
				log.debug("Creating PortalWatchdog scheduled task");
				portalWatchdog = scheduler.scheduleAtFixedRate(new PortalWatchdog(this), 200, 10000, TimeUnit.MILLISECONDS);
				stopped = false;
				return true;
			} catch (Exception e) {
				log.error("Couldn't start probe controller engine. Reason {}", e);
			}
		}
		return false;
	}

	@Override
	public boolean stop() {
		log.debug("Trying to stop probe process via process handle. Process handle available: {}", probeProcess == null);
		if (probeProcess != null) {
			try {
				log.debug("Calling destroy on probe process handle");
				sendStopCommand();
				TimeUnit.SECONDS.sleep(1);

				if (probeProcess.getProcess().isAlive()) {
					probeProcess.getProcess().destroy();
					log.debug("Destroyed Probe process.");
				} else {
					log.debug("Probe process exited using STOP command.");
				}

				if (!probeProcess.getProcess().isAlive()) {
					exitValue = probeProcess.getProcess().exitValue();
					log.debug("Exit value was {}", exitValue);
				}
			} catch (Exception e) {
				log.error("Stopping didn't work correctly! Reason {}", e);
			}
		}
		/*
		 * Shutting down internal threads.
		 */
		log.debug("Shutting down internal monitor, watchdog and updater tasks");
		try {
			if (probeMonitor != null) {
				probeMonitor.cancel(false);
			}
			if (probeUpdater != null) {
				probeUpdater.cancel(true);
			}
			if (portalWatchdog != null) {
				portalWatchdog.cancel(true);
			}

			log.debug("Internal monitor, watchdog and updater tasks have been shut down");
		} catch (Exception e) {
			log.error("Shuting down tasks didn't work.", e);
		}

		log.debug("Shutting down sheduler");
		try {
			shutdownSchedulers();
			log.debug("Scheduler have been shut down");
		} catch (InterruptedException e) {
			log.error("Shuting down scheduler didn't work.", e);
		}

		stopped = true;

		return true;
	}

	@Override
	public String getName() {
		return COMPLETE_NAME;
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

	@Override
	public void triggerRestart() {
		triggerScheduler.schedule(new TriggerEngineState(this, TriggerableEngineStates.RESTART), 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void triggerStart() {
		triggerScheduler.schedule(new TriggerEngineState(this, TriggerableEngineStates.START), 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void triggerStop() {
		triggerScheduler.schedule(new TriggerEngineState(this, TriggerableEngineStates.STOP), 0, TimeUnit.MILLISECONDS);
	}

	private void initializeSchedulers() {
		scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(processors);
		scheduler.setRemoveOnCancelPolicy(true);
		scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
	}

	private void shutdownSchedulers() throws InterruptedException {
		scheduler.shutdown();
		scheduler.shutdownNow();
	}

	private boolean startProbe() {
		log.debug("Starting probe process via invoking a java process");
		try {
			probeProcess = ProcessUtils.invokeJavaProcess("-jar", probeLibraryPath, "-l", licensePath, "-instrumentation");
			log.debug("Probe started using JAR. Alive {}", probeProcess.getProcess().isAlive());
			probeProcess.getObservable().addObserver(observer);
			probeProcess.startObserving();
			log.debug("Added output observer to probe process.");
			TimeUnit.SECONDS.sleep(5);
			log.debug("Waited 5 seconds before sending instrumentation command");
			sendStartCommand();
			return true;
		} catch (FileNotFoundException | InterruptedException ie) {
			log.error("Couldn't invoke Probe using standard parameters. Reason {}", ie);
		}
		return false;
	}

	public void sendStartCommand() {
		try {
			log.debug("Sending START service command to PROBE");
			probeProcess.instrumentService().sendCommand(new ServiceCommand(ServiceCommands.START));
		} catch (Exception e) {
			log.error("Couldn't send service command to PROBE! Reason {}", e.getMessage());
		}
	}

	public void sendStopCommand() {
		try {
			log.debug("Sending START service command to PROBE");
			probeProcess.instrumentService().sendCommand(new ServiceCommand(ServiceCommands.STOP));
		} catch (Exception e) {
			log.error("Couldn't send service command to PROBE! Reason {}", e.getMessage());
		}
	}

	public void sendCheckStatusCommand() {
		try {
			log.debug("Sending CHECK_STATUS service command to PROBE");
			probeProcess.instrumentService().sendCommand(new ServiceCommand(ServiceCommands.CHECK_STATUS));
		} catch (Exception e) {
			log.error("Couldn't send service command to PROBE! Reason {}", e.getMessage());
		}
	}

}
