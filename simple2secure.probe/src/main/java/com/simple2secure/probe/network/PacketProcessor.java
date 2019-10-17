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
package com.simple2secure.probe.network;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class PacketProcessor {

	private String name;

	final long ONE_MINUTE_IN_MILLIS = 60000;

	private Map<String, String> options;

	protected PacketContainer packet;

	public PacketProcessor(String name, Map<String, String> options) {
		if (options != null) {
			this.options = options;
		}
		this.name = name;
	}

	public void initialize(PacketContainer packet) {
		this.packet = packet;
	}

	public PacketContainer process() {
		PacketContainer container = processPacket();
		performAnalysis();
		return container;
	}

	public abstract PacketContainer processPacket();

	public abstract void performAnalysis();

	public String getName() {
		return name;
	}

	public Map<String, String> getOptions() {
		return options;
	}

	public boolean isAnalysisTimeExpired(long analysisInterval, Date analysisStartTime) throws ParseException {

		Date date = new Date();

		long currentDateInMS = date.getTime();

		long analysisStartDateInMS = analysisStartTime.getTime();

		long analysisEndDateinMS = analysisStartDateInMS + (analysisInterval * ONE_MINUTE_IN_MILLIS);

		if (currentDateInMS <= analysisEndDateinMS) {
			return false;
		} else {
			return true;
		}
	}

	public long getAnalysisIntervalInMinutes(long intervalTime, TimeUnit intervalUnit) {
		long analysisInterval = 0;

		if (intervalUnit == TimeUnit.HOURS) {
			analysisInterval = intervalTime * 60;
		} else if (intervalUnit == TimeUnit.DAYS) {
			analysisInterval = intervalTime * 60 * 24;
		} else {
			analysisInterval = intervalTime;
		}
		return analysisInterval;
	}
}
