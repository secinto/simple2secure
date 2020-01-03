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
package com.simple2secure.commons.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.service.ServiceInstrumentation;

public class ProcessContainer {

	private static Logger log = LoggerFactory.getLogger(ProcessContainer.class);

	private Process process;
	private ProcessStreamObservable observable;
	private ServiceInstrumentation serviceInstrumentation;

	public ProcessContainer(Process process, ProcessStreamObservable observable) {
		this.process = process;
		this.observable = observable;
	}

	public Process getProcess() {
		return process;
	}

	public ProcessStreamObservable getObservable() {
		return observable;
	}

	public void startObserving() {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		log.debug("Start observing with new single thread for {}", observable);
		pool.submit(observable);
		pool.shutdown();
	}

	public ServiceInstrumentation instrumentService() {
		if (serviceInstrumentation == null) {
			serviceInstrumentation = new ServiceInstrumentation(process.getOutputStream());
		}
		return serviceInstrumentation;
	}

}
