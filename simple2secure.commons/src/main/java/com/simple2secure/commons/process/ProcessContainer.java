package com.simple2secure.commons.process;

public class ProcessContainer {

	private Process process;
	private ProcessStream consumer;

	public ProcessContainer(Process process, ProcessStream consumer) {
		this.process = process;
		this.consumer = consumer;
	}

	public Process getProcess() {
		return process;
	}

	public ProcessStream getConsumer() {
		return consumer;
	}

}
