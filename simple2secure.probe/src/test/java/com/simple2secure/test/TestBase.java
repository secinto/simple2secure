package com.simple2secure.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.dao.ConfigDao;

public class TestBase {

	protected static Logger log = LoggerFactory.getLogger(TestBase.class);

	private boolean initialized = false;

	protected void initialize() {

		this.initialized = true;
	}

	protected void cleanup() {

	}

	protected ConfigDao getConfigDao() {
		return null;
	}
}
