package com.simple2secure.commons.process;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class ProcessStreamConsumer implements Runnable {
	private InputStream inputStream;
	private Consumer<String> consumer;

	public ProcessStreamConsumer(InputStream inputStream, Consumer<String> consumer) {
		this.inputStream = inputStream;
		this.consumer = consumer;
	}

	@Override
	public void run() {
		new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public Consumer<String> getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer<String> consumer) {
		this.consumer = consumer;
	}

}