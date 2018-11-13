package com.simple2secure.service.test;

import org.junit.jupiter.api.Test;

import com.simple2secure.service.ProbeServiceRunner;

public class TestStartProbeService {

	@Test
	public void testCreateProbeService() throws Exception {
		ProbeServiceRunner.startService("Test", "Test Service", "simple2secure.service-0.1.0-SNAPSHOT.jar",
				"com.simple2secure.service.ProbeControllerService");
	}
}
