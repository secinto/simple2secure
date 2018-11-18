package com.simple2secure.commons.process;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamGobbler implements Runnable {
	private static Logger log = LoggerFactory.getLogger(StreamGobbler.class);

	private InputStream inputStream;

	public StreamGobbler(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void run() {
		new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(log::debug);
	}
}
