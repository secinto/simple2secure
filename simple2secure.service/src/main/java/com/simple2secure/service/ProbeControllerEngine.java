package com.simple2secure.service;

public class ProbeControllerEngine implements Engine {
	private static final String VERSION = "0.1.0";
	private static final String NAME = "ProbeController Service";
	private static final String COMPLETE_NAME = NAME + ":" + VERSION;
	private boolean stopped = true;

	@Override
	public boolean isStopped() {
		return stopped;
	}

	@Override
	public boolean start() {
		stopped = false;

		return true;
	}

	@Override
	public boolean stop() {
		stopped = true;
		return stopped;
	}

	@Override
	public String getName() {
		return COMPLETE_NAME;
	}
}
