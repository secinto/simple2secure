package com.simple2secure.commons.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessContainer {

	private Process process;
	private ProcessStreamObservable observable;

	public ProcessContainer(Process process, ProcessStreamObservable observable) {
		this.process = process;
		this.observable = observable;
	}

	public Process getProcess() {
		return process;
	}

	public ProcessStreamObservable getObservable() {
		return observable;
	}

	public void startGobbling() {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.submit(observable);
		pool.shutdown();
	}

}
