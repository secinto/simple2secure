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
package com.simple2secure.commons.test;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import com.simple2secure.commons.process.LoggingObserver;
import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.commons.process.ProcessUtils;

public class TestCreateProcess {

	public static void main(String[] args) throws Exception {
		// ProcessUtils.createProcess("cmd.exe", "/c", "java", "-version");
		ProcessContainer container = ProcessUtils.createProcess("java", "-cp", "build\\libs\\simple2secure.commons-0.1.0.jar",
				"com.simple2secure.commons.process.EchoService");
		container.getObservable().addObserver(new LoggingObserver());
		container.startObserving();

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(container.getProcess().getOutputStream()));
		writer.write("Test\n");
		writer.flush();
		writer.write("TestMore\n");
		writer.flush();
		// writer.write("stop\n");
		writer.flush();
		writer.close();
		/*
		 * Give the service some time to get the input and process it. Otherwise this thread exits immediately
		 */
		Thread.sleep(2000);
		// container.getObservable().setRunning(false);
	}
}
