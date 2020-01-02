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

	private ConfigScheduler configScheduler;
	private ReportScheduler reportScheduler;
	private QueryScheduler queryScheduler;
	private NetworkScheduler networkScheduler;

	private boolean running = false;

	private long interval = TimeUnit.MINUTES.toMillis(1);

	public ProbeWorkerThread() {
		configScheduler = new ConfigScheduler();
		reportScheduler = new ReportScheduler();
		queryScheduler = new QueryScheduler();
		networkMonitor = NetworkMonitor.startMonitor();
		networkScheduler = new NetworkScheduler(networkMonitor);
		time = new Timer();

	}

	@Override
	public void run() {
		log.debug("ProbeWorkerThread running");
		log.debug("Starting network monitor");
		time.schedule(networkScheduler, 0, interval);
		log.debug("Scheduling the different tasks");
		time.schedule(configScheduler, 0, interval);
		time.schedule(queryScheduler, 100, interval);
		time.schedule(reportScheduler, 500, interval);

		running = true;

		while (running) {
			long currentTime = System.currentTimeMillis();
			/*
			 * Check if network scheduler is OK, if not restart it.
			 */
			if (networkScheduler != null) {
				if (networkScheduler.scheduledExecutionTime() < currentTime - interval) {
					networkScheduler.cancel();
					time.schedule(networkScheduler, 0, interval);
				}
			} else {
				if (networkMonitor == null) {
					networkMonitor = NetworkMonitor.startMonitor();
				}
				networkScheduler = new NetworkScheduler(networkMonitor);
			}

			/*
			 * Check if config scheduler is OK, if not restart it.
			 */
			if (configScheduler != null) {
				if (configScheduler.scheduledExecutionTime() < currentTime - interval) {
					configScheduler.cancel();
					time.schedule(configScheduler, 0, interval);
				}
			} else {
				configScheduler = new ConfigScheduler();
			}

			/*
			 * Check if query scheduler is OK, if not restart it.
			 */
			if (queryScheduler != null) {
				if (queryScheduler.scheduledExecutionTime() < currentTime - interval) {
					queryScheduler.cancel();
					time.schedule(queryScheduler, 200, interval);
				}
			} else {
				queryScheduler = new QueryScheduler();
			}
			/*
			 * Check if report scheduler is OK, if not restart it.
			 */
			if (reportScheduler != null) {
				if (reportScheduler.scheduledExecutionTime() < currentTime - interval) {
					reportScheduler.cancel();
					time.schedule(reportScheduler, 500, interval);
				}
			} else {
				reportScheduler = new ReportScheduler();
			}

			try {
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException ie) {
				log.error("Letting probe worker thread sleep failed. Reason {}", ie.getMessage());
				running = false;
			}

		}

	}

	public ConfigScheduler getConfigScheduler() {
		return configScheduler;
	}

	public ReportScheduler getReportScheduler() {
		return reportScheduler;
	}

	public QueryScheduler getQueryScheduler() {
		return queryScheduler;
	}

	public NetworkScheduler getNetworkScheduler() {
		return networkScheduler;
	}

	public boolean isRunning() {
		if (networkMonitor != null && networkScheduler != null && configScheduler != null && queryScheduler != null
				&& reportScheduler != null) {
			if (networkMonitor.isRunning() && running) {
				return true;
			}
		}
		return false;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
