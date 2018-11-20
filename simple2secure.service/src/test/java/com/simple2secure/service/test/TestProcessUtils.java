package com.simple2secure.service.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;

import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.commons.process.ProcessUtils;
import com.simple2secure.service.test.utils.TestLoggingObserver;

public class TestProcessUtils {

	private CountDownLatch lock = new CountDownLatch(1);

	@Test
	public void testDemoApplicationStart() throws Exception {
		ProcessUtils.invokeJavaProcess(null, false, "-cp", "../../release/simple2secure.service-0.1.0.jar",
				"com.simple2secure.service.test.EchoClient", "localhost", "8000");
	}

	@Test
	public void testCreateProcess() throws Exception {
		ProcessContainer container = ProcessUtils.createProcess("cmd.exe", "/c", "java", "-version");
		TestLoggingObserver observer = new TestLoggingObserver();
		container.getObservable().addObserver(observer);
		container.startObserving();
		// lock.await(2000, TimeUnit.MILLISECONDS);
		assertTrue(observer.getFirstObservable().contains("java version"));
		assertTrue(observer.getLastObservable().contains("Java HotSpot"));

	}
}
