package com.simple2secure.service.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import com.simple2secure.commons.service.ServiceUtils;

public class TestStartProbeService {

	@AfterAll
	public static void cleanup() {
		ServiceUtils.deleteService("Probe Test Service");
	}

	@Test
	public void testInstallService() {
		ServiceUtils.installService(System.getProperty("user.dir"), "Probe Test Service", "Probe Test Service",
				"release/simple2secure.service-0.1.0.jar", "com.simple2secure.service.ProbeControllerService",
				"windowsService", "com.simple2secure.service.ProbeControllerService", "windowsService");
	}
}
