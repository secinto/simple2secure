package com.simple2secure.commons.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.service.ServiceCommand;
import com.simple2secure.commons.service.ServiceCommands;

public class TestServiceCommand {
	private static Logger log = LoggerFactory.getLogger(TestServiceCommand.class);

	@Test
	public void testServiceCommandParsing() throws Exception {
		ServiceCommand command = ServiceCommand.fromString("Start");
		assertEquals(ServiceCommands.START, command.getCommand());
		assertNotEquals(ServiceCommands.STOP, command.getCommand());
	}
}
