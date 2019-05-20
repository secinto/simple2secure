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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessStreamObservable extends Observable implements Runnable {
	private static Logger log = LoggerFactory.getLogger(ProcessStreamObservable.class);

	private InputStream inputStream;

	private boolean running = false;

	public ProcessStreamObservable(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void run() {
		running = true;
		String line = "INIT";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			while ((line = reader.readLine()) != null && running) {
				setChanged();
				notifyObservers(line);
			}
			if (!running) {
				reader.close();
			}
			log.debug("Observable is exiting");
		} catch (Exception e) {
			log.error("Reading output from process failed. Reason {}", e);
		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
