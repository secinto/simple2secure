package com.simple2secure.service.test;

import org.junit.jupiter.api.Test;

import com.simple2secure.commons.service.ServiceUtils;

public class TestStartProbeService {

	@Test
	public void cleanup() {
		ServiceUtils.deleteService("Probe Service");
	}

	@Test
	public void testInstallService() {
		ServiceUtils.installService(true, System.getProperty("user.dir") + "\\release", "Probe Service", "Probe Service",
				"simple2secure.service-0.1.0.jar", "com.simple2secure.service.ProbeControllerService", "windowsService",
				"com.simple2secure.service.ProbeControllerService", "windowsService");
	}
}
