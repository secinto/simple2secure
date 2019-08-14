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
package com.simple2secure.service.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.service.interfaces.ControllerEngine;
import com.simple2secure.service.model.ServiceTask;

public class ProbeMonitor extends ServiceTask {

	private Logger log = LoggerFactory.getLogger(ProbeMonitor.class);

	private ProcessContainer probeProcess;

	public ProbeMonitor(ControllerEngine probeControllerEngine) {
		super(probeControllerEngine);
		probeProcess = probeControllerEngine.getControlledProcess();
	}

	@Override
	public void run() {
		boolean restart = false;
		log.debug("Executing {}", ProbeMonitor.class);
		log.debug("Probe process is alive {}", probeProcess.getProcess().isAlive());

		if (probeProcess == null) {
			restart = true;
		} else {
			if (probeProcess.getProcess() == null) {
				restart = true;
			} else if (!probeProcess.getProcess().isAlive()) {
				restart = true;
			}
		}

		if (restart) {
			log.info("Controlled probe process is not alive anymore, trying to restart it!");

			if (!this.getProbeControllerEngine().isStopped()) {
				log.debug("Triggering restart for controlled probe process!");
				this.getProbeControllerEngine().triggerRestart();
			} else {
				log.debug("Triggering start for controlled probe process!");
				this.getProbeControllerEngine().triggerStart();
			}
		}
	}

}
