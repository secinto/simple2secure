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
package com.simple2secure.service.observer;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLoggingObserver implements Observer {
	private static Logger log = LoggerFactory.getLogger(SimpleLoggingObserver.class);

	private String firstObservable = null;
	private String lastObservable = null;

	@Override
	public void update(Observable o, Object arg) {

		log.debug("Observable output: {}", arg);

		if (firstObservable == null) {
			firstObservable = (String) arg;
		} else {
			lastObservable = (String) arg;
		}
	}

	public String getFirstObservable() {
		return firstObservable;
	}

	public String getLastObservable() {
		return lastObservable;
	}

}
