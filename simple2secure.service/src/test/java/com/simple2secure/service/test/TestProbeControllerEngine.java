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

import com.simple2secure.service.engine.ProbeControllerEngine;
import com.simple2secure.service.model.ProbeProcessInteraction;

public class TestProbeControllerEngine {

	@Test
	public void testProbeControllerEngineStartWithPath() throws Exception {
		ProbeControllerEngine engine = new ProbeControllerEngine(System.getProperty("user.dir") + "\\release\\libs\\simple2secure.probe.jar",
				System.getProperty("user.dir") + "\\release\\license");
		engine.start();
		ProbeProcessInteraction.getInstance(engine.getControlledProcess()).sendStartCommand();
		engine.stop();
	}

	@Test
	public void testProbeControllerEngineStartDefault() throws Exception {
		ProbeControllerEngine engine = new ProbeControllerEngine();
		engine.start();
		engine.stop();
	}

}
