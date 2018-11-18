package com.simple2secure.commons.process;

public class ProcessContainer {

	private Process process;
	private ProcessGobbler consumer;

	public ProcessContainer(Process process, ProcessGobbler consumer) {
		this.process = process;
		this.consumer = consumer;
	}

	public Process getProcess() {
		return process;
	}

	public ProcessGobbler getConsumer() {
		return consumer;
	}

}
