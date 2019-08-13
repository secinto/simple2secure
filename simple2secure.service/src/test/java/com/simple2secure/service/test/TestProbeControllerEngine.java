package com.simple2secure.service.test;

import org.junit.jupiter.api.Test;

import com.simple2secure.service.ProbeControllerEngine;

public class TestProbeControllerEngine {

	@Test
	public void testProbeControllerEngineStartDefault() throws Exception {
		ProbeControllerEngine engine = new ProbeControllerEngine(
				System.getProperty("user.dir") + "\\release\\libs\\simple2secure.probe.jar",
				System.getProperty("user.dir") + "\\release\\license.zip");
		engine.start();
		engine.stop();
	}
}
