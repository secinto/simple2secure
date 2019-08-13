package com.simple2secure.service.interfaces;

public interface Engine {

	/**
	 * Returns the status of the engine if it is either running or stopped.
	 * 
	 * @return
	 */
	public boolean isStopped();

	/**
	 * Blocking process which initializes and starts all required tasks, threads and
	 * processes and returns the status of the start operation afterwards. This
	 * can't be called from any of the scheduled tasks since they are itself
	 * controlled by this engine.
	 * 
	 * @return
	 */
	public boolean start();

	/**
	 * Blocking process which stops and deletes all required tasks, threads and
	 * processes and returns the status of the stop operation afterwards. This can't
	 * be called from any of the scheduled tasks since they are itself controlled by
	 * this engine.
	 * 
	 * @return
	 */
	public boolean stop();

	/**
	 * Returns the name of the engine.
	 * 
	 * @return
	 */
	public String getName();
}
