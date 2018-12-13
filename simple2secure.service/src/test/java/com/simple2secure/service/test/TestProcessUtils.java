package com.simple2secure.service.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.commons.process.ProcessUtils;
import com.simple2secure.service.test.utils.TestLoggingObserver;

public class TestProcessUtils {

	private CountDownLatch lock = new CountDownLatch(1);

	@Test
	public void testCreateProcess() throws Exception {
		ProcessContainer container = ProcessUtils.createProcess("java", "-version");
		TestLoggingObserver observer = new TestLoggingObserver();
		container.getObservable().addObserver(observer);
		container.startObserving();
		lock.await(2000, TimeUnit.MILLISECONDS);
		assertTrue(observer.getFirstObservable().contains("java version"));
		assertTrue(observer.getLastObservable().contains("Java HotSpot"));

	}
}
