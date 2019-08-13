package com.simple2secure.service.observer;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLoggingObserver implements Observer {
	private static Logger log = LoggerFactory.getLogger(SimpleLoggingObserver.class);

	private String firstObservable = null;
	private String lastObservable = null;

	@Override
	public void update(Observable o, Object arg) {

		log.debug("Observable output: {}", arg);

		if (firstObservable == null) {
			firstObservable = (String) arg;
		} else {
			lastObservable = (String) arg;
		}
	}

	public String getFirstObservable() {
		return firstObservable;
	}

	public String getLastObservable() {
		return lastObservable;
	}

}
