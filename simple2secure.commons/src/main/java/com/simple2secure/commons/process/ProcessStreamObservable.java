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
		log.debug("Observable is starting");
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
