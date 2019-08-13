package com.simple2secure.service.interfaces;

import java.util.TimerTask;

public abstract class ServiceTask extends TimerTask {

	private ControllerEngine probeControllerEngine;

	public ServiceTask(ControllerEngine probeControllerEngine) {
		this.probeControllerEngine = probeControllerEngine;
	}

	public ControllerEngine getProbeControllerEngine() {
		return probeControllerEngine;
	}

	public void setProbeControllerEngine(ControllerEngine probeControllerEngine) {
		this.probeControllerEngine = probeControllerEngine;
	}

}
