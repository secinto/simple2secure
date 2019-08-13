package com.simple2secure.commons.process;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessStreamObservable extends Observable implements Runnable {
	private static Logger log = LoggerFactory.getLogger(ProcessStreamObservable.class);

	private InputStream inputStream;

	private boolean running = false;

	public ProcessStreamObservable(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void run() {
		running = true;
		String line = "INIT";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			while ((line = reader.readLine()) != null && running) {
				setChanged();
				log.debug("ProcessStream produced: {}", line);
				notifyObservers(line);
			}
			if (!running) {
				reader.close();
			}
			log.debug("Observable is exiting");
		} catch (Exception e) {
			log.error("Reading output from process failed. Reason {}", e);
		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
