package com.simple2secure.commons.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.service.ServiceCommands;

public class TestServiceCommands {
	private static Logger log = LoggerFactory.getLogger(TestServiceCommands.class);

	@Test
	public void testServiceCommandResposneParsing() throws Exception {
		assertFalse(ServiceCommands.CHECK_STATUS.checkResponsePositive(ServiceCommands.CHECK_STATUS.getNegativeCommandResponse()));
		assertTrue(ServiceCommands.CHECK_STATUS.checkResponsePositive(ServiceCommands.CHECK_STATUS.getPositiveCommandResponse()));
		assertFalse(ServiceCommands.CHECK_STATUS.checkResponsePositive(""));
		assertFalse(ServiceCommands.CHECK_STATUS.checkResponsePositive(null));
		assertFalse(ServiceCommands.CHECK_STATUS.checkResponsePositive("Some String"));
		assertFalse(ServiceCommands.CHECK_STATUS.checkResponsePositive("CHECK_STATUS = OK"));
		assertTrue(ServiceCommands.CHECK_STATUS.checkResponsePositive("CHECK_STATUS : OK"));
	}
}
