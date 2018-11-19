package com.simple2secure.service.test.utils;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLoggingObserver implements Observer {
	private static Logger log = LoggerFactory.getLogger(TestLoggingObserver.class);

	private String firstObservable = null;
	private String lastObservable = null;

	@Override
	public void update(Observable o, Object arg) {

		log.debug("Process output {}", arg);

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
