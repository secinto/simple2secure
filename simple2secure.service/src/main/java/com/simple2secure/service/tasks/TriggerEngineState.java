package com.simple2secure.service.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.service.interfaces.ControllerEngine;
import com.simple2secure.service.interfaces.ServiceTask;

public class TriggerEngineState extends ServiceTask {

	private static Logger log = LoggerFactory.getLogger(TriggerEngineState.class);

	private TriggerableEngineStates stateToTrigger;

	public TriggerEngineState(ControllerEngine probeControllerEngine, TriggerableEngineStates stateToTrigger) {
		super(probeControllerEngine);
		this.stateToTrigger = stateToTrigger;
	}

	@Override
	public void run() {
		log.debug("Triggering change of engine state");
		switch (stateToTrigger) {
		case START:
			log.debug("Engine state START triggered");
			this.getProbeControllerEngine().start();
			log.debug("Engine state START finished");
			break;
		case STOP:
			log.debug("Engine state STOP triggered");
			this.getProbeControllerEngine().stop();
			log.debug("Engine state STOP finished");
			break;
		case RESTART:
			log.debug("Engine state RESTART performed");
			log.debug("Engine state STOP triggered");
			this.getProbeControllerEngine().stop();
			log.debug("Engine state STOP finished");
			log.debug("Engine state START triggered");
			this.getProbeControllerEngine().start();
			log.debug("Engine state START finished");
			break;
		default:
			log.info("No default action specified for triggering an engine state!");
			break;
		}
	}

}
