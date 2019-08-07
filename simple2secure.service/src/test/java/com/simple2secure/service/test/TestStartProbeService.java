package com.simple2secure.service.test;

import com.simple2secure.commons.service.ServiceUtils;

public class TestStartProbeService {

	public static void cleanup() {
		ServiceUtils.deleteService("Probe Service");
	}

	public void testInstallService() {
		ServiceUtils.installService(System.getProperty("user.dir"), "Probe Service", "Probe Service",
				"release\\simple2secure.service-0.1.0.jar", "com.simple2secure.service.ProbeControllerService", "windowsService",
				"com.simple2secure.service.ProbeControllerService", "windowsService");
	}
}
