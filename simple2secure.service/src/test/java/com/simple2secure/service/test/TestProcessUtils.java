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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.commons.process.ProcessUtils;
import com.simple2secure.service.observer.SimpleLoggingObserver;

public class TestProcessUtils {

	private CountDownLatch lock = new CountDownLatch(1);

	@Test
	public void testCreateProcess() throws Exception {
		ProcessContainer container = ProcessUtils.createProcess("java", "-version");
		SimpleLoggingObserver observer = new SimpleLoggingObserver();
		container.getObservable().addObserver(observer);
		container.startObserving();
		lock.await(2000, TimeUnit.MILLISECONDS);
		assertTrue(observer.getFirstObservable().contains("java version"));
		assertTrue(observer.getLastObservable().contains("Java HotSpot"));

	}
}
