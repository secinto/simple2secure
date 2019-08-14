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
package com.simple2secure.api.model;

import java.util.concurrent.TimeUnit;

public class TestObjWeb {

	private String podId;
	private String testId;
	private String hostname;
	private boolean active;
	private String name;
	private TestContent test_content;
	private boolean scheduled;
	private long scheduledTime;
	private TimeUnit scheduledTimeUnit;

	public TestObjWeb() {

	}

	public TestObjWeb(String name, TestContent test_content) {
		super();
		this.name = name;
		this.test_content = test_content;
	}

	public String getPodId() {
		return podId;
	}

	public void setPodId(String podId) {
		this.podId = podId;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	public long getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(long scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public TimeUnit getScheduledTimeUnit() {
		return scheduledTimeUnit;
	}

	public void setScheduledTimeUnit(TimeUnit scheduledTimeUnit) {
		this.scheduledTimeUnit = scheduledTimeUnit;
	}

	public TestContent getTest_content() {
		return test_content;
	}

	public void setTest_content(TestContent test_content) {
		this.test_content = test_content;
	}
}
