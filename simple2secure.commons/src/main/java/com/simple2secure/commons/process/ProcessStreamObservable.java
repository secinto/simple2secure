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

	public ProcessStreamObservable(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void run() {
		String line = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			while ((line = reader.readLine()) != null) {
				setChanged();
				notifyObservers(line);
			}
		} catch (Exception e) {
			log.error("Reading output from process failed. Reason {}", e);
		}
	}

}
