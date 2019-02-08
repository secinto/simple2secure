package com.simple2secure.service;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.simple2secure.commons.service.ServiceInstrumentation;
import com.simple2secure.service.interfaces.Engine;
import com.simple2secure.service.tasks.PortalWatchdog;
import com.simple2secure.service.tasks.ProbeMonitor;
import com.simple2secure.service.tasks.ProbeUpdater;
import com.simple2secure.service.test.utils.TestLoggingObserver;

public class ProbeControllerEngine implements Engine {
	private static Logger log = LoggerFactory.getLogger(ProbeControllerEngine.class);

	private static final String VERSION = "0.1.0";
	private static final String NAME = "ProbeController Service";
	private static final String COMPLETE_NAME = NAME + ":" + VERSION;

	private ScheduledThreadPoolExecutor scheduler;
	private ServiceInstrumentation serviceInstrument;
	private ScheduledFuture<?> probeMonitor;
	private ScheduledFuture<?> probeUpdater;
	private ScheduledFuture<?> portalWatchdog;

	private ProcessContainer probeProcess;
	private TestLoggingObserver observer;
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

		observer = new TestLoggingObserver();

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
		if (startProbe()) {
			try {
				probeMonitor = scheduler.scheduleAtFixedRate(new ProbeMonitor(), 0, 1000, TimeUnit.MILLISECONDS);
				probeUpdater = scheduler.scheduleAtFixedRate(new ProbeUpdater(), 100, 1000, TimeUnit.MILLISECONDS);
				portalWatchdog = scheduler.scheduleAtFixedRate(new PortalWatchdog(), 200, 1000, TimeUnit.MILLISECONDS);
				return true;
			} catch (Exception e) {
				log.error("Couldn't start probe controller engine. Reason {}", e);
			}
		}
		return false;
	}

	private boolean startProbe() {
		try {
			/*
			 * Starting probe using ProbeCLI and the initial license.
			 */
			probeProcess = ProcessUtils.invokeJavaProcess("-cp", "./release/simple2secure.probe-0.1.0.jar",
					"com.simple2secure.probe.cli.ProbeCLI", "-l", "license.zip");

			serviceInstrument = new ServiceInstrumentation(probeProcess.getProcess().getInputStream(),
					probeProcess.getProcess().getOutputStream());

			probeProcess.getObservable().addObserver(observer);
			probeProcess.startObserving();

			/*
			 * Sending to probe that it should start monitoring.
			 */
			serviceInstrument.sendCommand(new ServiceCommand(ServiceCommands.START, null));

		} catch (FileNotFoundException fnfe) {
			log.error("Couldn't create Probe process using standard parameters. Reason {}", fnfe);
		} catch (IOException e) {
			log.error("Couldn't send start command to created probe process. Reason {}", e);
		}
		return false;
	}

	@Override
	public boolean stop() {
		if (probeProcess != null) {
			probeProcess.getProcess().destroy();
			exitValue = probeProcess.getProcess().exitValue();
			log.debug("Destroyed Probe process. Exit value was {}", exitValue);
		}
		scheduler.shutdown();
		return true;
	}

	@Override
	public String getName() {
		return COMPLETE_NAME;
	}
}
