package com.simple2secure.service.observer;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.service.ServiceCommands;

public class CheckStatusObserver implements Observer {
	private static Logger log = LoggerFactory.getLogger(SimpleLoggingObserver.class);

	private boolean checkStatusOK = false;
	private boolean checkStatusResponseReceived = false;

	@Override
	public void update(Observable o, Object arg) {

		log.debug("Observable output: {}", arg);
		String receivedServiceResponse = (String) arg;
		if (receivedServiceResponse.startsWith(ServiceCommands.CHECK_STATUS.getName())) {
			checkStatusResponseReceived = true;
			if (ServiceCommands.CHECK_STATUS.checkResponsePositive(receivedServiceResponse)) {
				checkStatusOK = true;
			}
		}
	}

	public boolean isCheckStatusResponseReceived() {
		return checkStatusResponseReceived;
	}

	public boolean isCheckStatusOK() {
		return checkStatusOK;
	}

	public CheckStatusObserver reset() {
		checkStatusOK = false;
		checkStatusResponseReceived = false;
		return this;
	}

}
