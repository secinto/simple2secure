package com.simple2secure.service.test;

import org.junit.jupiter.api.Test;

import com.simple2secure.service.ProbeServiceRunner;

public class TestStartProbeService {

	@Test
	public void testCreateProbeService() throws Exception {
		ProbeServiceRunner.installService("Test", "Test Service", "simple2secure.service-0.1.0.jar",
				"com.simple2secure.service.ProbeControllerService");
	}
}
