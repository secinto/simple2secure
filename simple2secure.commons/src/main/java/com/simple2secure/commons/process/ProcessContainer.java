package com.simple2secure.commons.process;

public class ProcessContainer {

	private Process process;
	private ProcessStreamConsumer consumer;

	public ProcessContainer(Process process, ProcessStreamConsumer consumer) {
		this.process = process;
		this.consumer = consumer;
	}

}
