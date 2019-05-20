/**
 *********************************************************************
 *
 * Copyright (C) 2019 by secinto GmbH (http://www.secinto.com)
 *
 *********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 *********************************************************************
 */
package com.simple2secure.commons.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessContainer {

	private Process process;
	private ProcessStreamObservable observable;

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
		pool.submit(observable);
		pool.shutdown();
	}

}
