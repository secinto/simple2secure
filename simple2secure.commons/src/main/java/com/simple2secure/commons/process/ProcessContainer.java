package com.simple2secure.commons.process;

import java.util.concurrent.Executors;

public class ProcessContainer {

	private Process process;
	private ProcessStreamObservable gobbler;

	public ProcessContainer(Process process, ProcessStreamObservable gobbler) {
		this.process = process;
		this.gobbler = gobbler;
	}

	public Process getProcess() {
		return process;
	}

	public ProcessStreamObservable getGobbler() {
		return gobbler;
	}

	public void startGobbling() {
		Executors.newSingleThreadExecutor().submit(gobbler);
	}

}
