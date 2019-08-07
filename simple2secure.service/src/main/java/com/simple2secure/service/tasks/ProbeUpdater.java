package com.simple2secure.service.tasks;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProbeUpdater extends TimerTask {
	private Logger log = LoggerFactory.getLogger(ProbeUpdater.class);

	public ProbeUpdater() {

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		log.debug("Executing {}", ProbeUpdater.class);
	}

}
