package com.simple2secure.service.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.service.interfaces.ControllerEngine;
import com.simple2secure.service.interfaces.ServiceTask;

public class PortalWatchdog extends ServiceTask {

	public PortalWatchdog(ControllerEngine probeControllerEngine) {
		super(probeControllerEngine);
	}

	private Logger log = LoggerFactory.getLogger(PortalWatchdog.class);

	@Override
	public void run() {
		log.debug("Executing {}", PortalWatchdog.class);

	}

}
