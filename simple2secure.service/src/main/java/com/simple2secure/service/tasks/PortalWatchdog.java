package com.simple2secure.service.tasks;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortalWatchdog extends TimerTask {
	private Logger log = LoggerFactory.getLogger(PortalWatchdog.class);

	@Override
	public void run() {
		log.debug("Executing {}", ProbeUpdater.class);

	}

}
