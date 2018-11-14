package com.simple2secure.service;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProbeControllerService implements Daemon {

	private static Logger log = LoggerFactory.getLogger(ProbeControllerService.class);

	private static ProbeControllerService engineLauncherInstance = new ProbeControllerService();
	private static ProbeControllerEngine engine = new ProbeControllerEngine();

	/**
	 * Static methods called by prunsrv to start/stop the Windows service. Pass the argument "start" to start the service, and pass "stop" to
	 * stop the service.
	 *
	 * @param args
	 *          Arguments from prunsrv command line
	 **/
	public static void windowsService(String args[]) {
		String cmd = "start";
		if (args.length > 0) {
			cmd = args[0];
		}
		System.out.println("Starting service");
		if ("start".equals(cmd)) {
			engineLauncherInstance.windowsStart();
		} else {
			engineLauncherInstance.windowsStop();
		}
	}

	public void windowsStart() {
		log.debug("windowsStart called");
		initialize();
		while (!engine.isStopped()) {
			// don't return until stopped
			synchronized (this) {
				try {
					this.wait(60000); // wait 1 minute and check if stopped
				} catch (InterruptedException ie) {
					log.error("The execution of {} has been interrupted because {}", engine.getName(), ie);
				}
			}
		}
	}

	public void windowsStop() {
		log.debug("windowsStop called");
		terminate();
		synchronized (this) {
			// stop the start loop
			this.notify();
		}
	}

	@Override
	public void init(DaemonContext arg0) throws Exception {
		log.debug("Daemon init");
	}

	@Override
	public void start() {
		log.debug("Daemon start");
		initialize();
	}

	@Override
	public void stop() {
		log.debug("Daemon stop");
		terminate();
	}

	@Override
	public void destroy() {
		log.debug("Daemon destroy");
	}

	/**
	 * Do the work of starting the engine
	 */
	private void initialize() {
		if (engine == null) {
			engine = new ProbeControllerEngine();
		} else {
			log.info("Starting {}", engine.getName());
			if (engine.start()) {
				log.info("{} started successfully!", engine.getName());
			} else {
				log.error("Couldn't start {}", engine.getName());
			}
		}
	}

	/**
	 * Cleanly stop the engine.
	 */
	public void terminate() {
		if (engine != null) {
			log.info("Stopping {}", engine.getName());
			if (engine.stop()) {
				log.info("{} stopped", engine.getName());
			} else {
				log.error("Couldn't stop {}", engine.getName());
			}
		}
	}
}
