package com.simple2secure.service.test;

import org.junit.jupiter.api.Test;

import com.simple2secure.service.ProbeControllerEngine;

public class TestProbeControllerEngine {

	@Test
	public void testProbeControllerEngineStartDefault() throws Exception {
		ProbeControllerEngine engine = new ProbeControllerEngine();
		engine.start();
	}
}
