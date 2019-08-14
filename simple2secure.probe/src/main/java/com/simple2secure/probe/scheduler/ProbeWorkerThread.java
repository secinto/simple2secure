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
package com.simple2secure.probe.scheduler;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.network.NetworkMonitor;

public class ProbeWorkerThread extends Thread {

	private static Logger log = LoggerFactory.getLogger(ProbeWorkerThread.class);

	final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

	static NetworkMonitor networkMonitor;

	static Timer time;

	@Override
	public void run() {
		log.debug("ProbeWorkerThread running");

		networkMonitor = NetworkMonitor.startMonitor();

		time = new Timer();

		time.schedule(new ConfigScheduler(), 0, TimeUnit.MINUTES.toMillis(1));
		time.schedule(new ReportScheduler(), 0, TimeUnit.MINUTES.toMillis(1));
		time.schedule(new NetworkScheduler(networkMonitor), 0, TimeUnit.MINUTES.toMillis(1));
		time.schedule(new QueryScheduler(), 0, TimeUnit.MINUTES.toMillis(1));

	}
}
