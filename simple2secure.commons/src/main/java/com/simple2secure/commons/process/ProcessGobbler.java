package com.simple2secure.commons.process;

import java.util.concurrent.Executors;

public class ProcessGobbler {
	private StreamGobbler inputGobbler;

	public ProcessGobbler(StreamGobbler inputGobbler) {
		this.inputGobbler = inputGobbler;
	}

	public void startGobbling() {
		Executors.newSingleThreadExecutor().submit(inputGobbler);
	}

}