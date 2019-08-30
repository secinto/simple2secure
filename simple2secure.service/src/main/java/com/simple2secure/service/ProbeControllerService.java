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
package com.simple2secure.service;

import java.util.Arrays;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.service.engine.ProbeControllerEngine;

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
		log.info("Received windowsService call");
		String cmd = "start";
		log.debug("Arguments available {}", args.length);
		if (args.length > 0) {
			cmd = args[0];
			log.debug("First argument used {}", args[0]);
		}

		log.debug("windowsService parameters", Arrays.toString(args));
		log.debug("Starting service");
		if ("start".equals(cmd)) {
			log.debug("Starting engine");
			engineLauncherInstance.windowsStart();
		} else {
			log.debug("Stoping engine");
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
					log.debug("Finished waiting for checking engine status");
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
		}

		log.info("Starting ProbeControllerEngine");
		if (engine.start()) {
			log.info("{} started successfully!", engine.getName());
		} else {
			log.error("Couldn't start {}", engine.getName());
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
