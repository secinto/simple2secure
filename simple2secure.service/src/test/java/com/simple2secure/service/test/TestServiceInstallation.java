/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
package com.simple2secure.service.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.service.ServiceUtils;

public class TestServiceInstallation {
	private static Logger log = LoggerFactory.getLogger(TestServiceInstallation.class);

	private static String serviceName = "Probe Service";

	private static String startServiceClassName = "com.simple2secure.service.ProbeControllerService";
	private static String stopServiceClassName = "com.simple2secure.service.ProbeControllerService";

	private static String libraryFilename = "libs\\simple2secure.service.jar";
	private static String startMethod = "windowsService";
	private static String stopMethod = "windowsService";

	private String libraryPath = System.getProperty("user.dir") + "\\release";

	@Test
	public void testDeleteService() {
		ServiceUtils.stopService(serviceName);
		ServiceUtils.deleteService(serviceName);
	}

	@Test
	public void testStartService() {
		ServiceUtils.startService(serviceName);
	}

	@Test
	public void testStopService() {
		ServiceUtils.stopService(serviceName);
	}

	@Test
	public void testInstallService() {
		log.info("Using library path {}", libraryPath);

		ServiceUtils.installService(true, libraryPath, serviceName, serviceName, libraryFilename, startServiceClassName, startMethod,
				stopServiceClassName, stopMethod);
	}
}
