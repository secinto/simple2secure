package com.simple2secure.service.test;

import org.junit.jupiter.api.Test;

import com.simple2secure.commons.service.ServiceUtils;

public class TestStartProbeService {

	@Test
	public void testRemoveService() {
		ServiceUtils.deleteService("ProbeService");
	}

	@Test
	public void testInstallService() {
		ServiceUtils.installService(System.getProperty("user.dir"), "ProbeService", "ProbeService", "simple2secure.service-0.1.0.jar",
				"com.simple2secure.service.ProbeControllerService", "windowsService", "com.simple2secure.service.ProbeControllerService",
				"windowsService");

	}
}
