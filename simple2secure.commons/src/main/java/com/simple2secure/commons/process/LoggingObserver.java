package com.simple2secure.commons.process;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class LoggingObserver implements Observer {

	private static Logger log = LoggerFactory.getLogger(LoggingObserver.class);

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			String processOutput = (String) arg;
			if (!Strings.isNullOrEmpty(processOutput)) {
				log.debug("Process output {}", processOutput);
			}
		}
	}

}
