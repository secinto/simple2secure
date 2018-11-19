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
		container.getGobbler().addObserver(new LoggingObserver());
		container.startGobbling();

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(container.getProcess().getOutputStream()));
		writer.write("Test\n");
		writer.flush();
		writer.write("TestMore\n");
		writer.flush();
		writer.write("stop\n");
		writer.flush();
		writer.close();
	}
}
