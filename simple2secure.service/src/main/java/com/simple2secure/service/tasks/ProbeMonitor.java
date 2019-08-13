package com.simple2secure.service.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.service.interfaces.ControllerEngine;
import com.simple2secure.service.interfaces.ServiceTask;

public class ProbeMonitor extends ServiceTask {

	private Logger log = LoggerFactory.getLogger(ProbeMonitor.class);

	private ProcessContainer probeProcess;

	public ProbeMonitor(ControllerEngine probeControllerEngine) {
		super(probeControllerEngine);
		this.probeProcess = probeControllerEngine.getControlledProcess();
	}

	@Override
	public void run() {
		boolean restart = false;
		log.debug("Executing {}", ProbeMonitor.class);
		log.debug("Probe process is alive {}", this.probeProcess.getProcess().isAlive());

		if (this.probeProcess == null) {
			restart = true;
		} else {
			if (this.probeProcess.getProcess() == null) {
				restart = true;
			} else if (!this.probeProcess.getProcess().isAlive()) {
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
