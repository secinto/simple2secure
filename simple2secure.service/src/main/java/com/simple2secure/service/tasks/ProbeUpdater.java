package com.simple2secure.service.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.service.interfaces.ControllerEngine;
import com.simple2secure.service.interfaces.ServiceTask;

public class ProbeUpdater extends ServiceTask {
	private Logger log = LoggerFactory.getLogger(ProbeUpdater.class);

	public ProbeUpdater(ControllerEngine controllerEngine) {
		super(controllerEngine);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		log.debug("Executing {}", ProbeUpdater.class);
	}

}
