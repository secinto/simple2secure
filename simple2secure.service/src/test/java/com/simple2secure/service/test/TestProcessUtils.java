package com.simple2secure.service.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.simple2secure.commons.process.ProcessUtils;

public class TestProcessUtils {

	@Test
	public void testDemoApplicationStart() throws Exception {
		List<String> arguments = new ArrayList<String>();
		arguments.add(
				"-cp ../../release/simple2secure.service-0.1.0.jar com.simple2secure.service.test.EchoClient localhost 8000");
		ProcessUtils.invokeJavaProcess(arguments, null, false);
	}

	@Test
	public void testCreateProcess() throws Exception {
		ProcessUtils.createProcess("cmd.exe", null, false);
	}
}
