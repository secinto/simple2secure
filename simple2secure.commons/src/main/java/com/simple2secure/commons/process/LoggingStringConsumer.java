package com.simple2secure.commons.process;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingStringConsumer implements Consumer<String> {
	private static Logger log = LoggerFactory.getLogger(LoggingStringConsumer.class);

	@Override
	public void accept(String data) {
		log.debug(data);
	}

}
