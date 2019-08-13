package com.simple2secure.commons.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessContainer {

	private static Logger log = LoggerFactory.getLogger(ProcessContainer.class);

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

	public void startObserving() {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		log.debug("Start observing with new single thread for {}", observable);
		pool.submit(observable);
		pool.shutdown();
	}

}
