package com.simple2secure.service.interfaces;

import com.simple2secure.commons.process.ProcessContainer;

public interface ControllerEngine extends Engine {

	/**
	 * Returns the {@link ProcessContainer} of the process controlled by this
	 * engine.
	 * 
	 * @return
	 */
	public ProcessContainer getControlledProcess();

	/**
	 * Triggers a restart of the engine in a non-blocking manner, thus this can be
	 * used from the scheduled tasks.
	 */
	public void triggerRestart();

	/**
	 * Triggers a start of the engine in a non-blocking manner, thus this can be
	 * used from the scheduled tasks.
	 */
	public void triggerStart();

	/**
	 * Triggers a stop of the engine in a non-blocking manner, thus this can be used
	 * from the scheduled tasks.
	 */
	public void triggerStop();

}
