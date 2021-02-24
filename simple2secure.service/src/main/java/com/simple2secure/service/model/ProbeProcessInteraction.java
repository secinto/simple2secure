package com.simple2secure.service.model;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.commons.service.ServiceCommand;
import com.simple2secure.commons.service.ServiceCommands;
import com.simple2secure.service.observer.CheckStatusObserver;

public class ProbeProcessInteraction {
	private static Logger log = LoggerFactory.getLogger(ProbeProcessInteraction.class);

	private ProcessContainer probeProcess;

	private static ProbeProcessInteraction instance;

	public static ProbeProcessInteraction getInstance(ProcessContainer probeProcess) {
		if (instance == null) {
			instance = new ProbeProcessInteraction(probeProcess);
		}

		return instance;
	}

	private ProbeProcessInteraction(ProcessContainer probeProcess) {
		if (probeProcess != null && probeProcess.getProcess() != null && probeProcess.getProcess().isAlive()) {
			this.probeProcess = probeProcess;
			log.info("ProbeProcessInteraction created successfully");
		} else {
			throw new IllegalArgumentException("Provided ProcessContainer is not valid");
		}
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
			log.debug("Sending STOP service command to PROBE");
			probeProcess.instrumentService().sendCommand(new ServiceCommand(ServiceCommands.STOP));
		} catch (Exception e) {
			log.error("Couldn't send service command to PROBE! Reason {}", e.getMessage());
		}
	}

	public boolean sendCheckStatusCommand() {
		try {
			log.debug("Sending CHECK_STATUS service command to PROBE");
			CheckStatusObserver checkStatusObserver = new CheckStatusObserver();
			probeProcess.getObservable().addObserver(checkStatusObserver);
			log.debug("Adding CHECK_STATUS observer");
			probeProcess.instrumentService().sendCommand(new ServiceCommand(ServiceCommands.CHECK_STATUS));
			log.debug("Waiting for CHECK_STATUS to return response");
			TimeUnit.SECONDS.sleep(5);
			if (checkStatusObserver.isCheckStatusResponseReceived()) {
				log.debug("CHECK_STATUS has finished");
				return checkStatusObserver.isCheckStatusOK();
			} else {
				log.debug("CHECK_STATUS has not finished, probably service if overloaded");
				return false;
			}
		} catch (InterruptedException e) {
			log.error("Waiting for PROBE to answer to status check was not successful. Reason {}", e.getMessage());
		} catch (Exception e) {
			log.error("Couldn't send service command to PROBE! Reason {}", e.getMessage());
		}
		return false;
	}
}
