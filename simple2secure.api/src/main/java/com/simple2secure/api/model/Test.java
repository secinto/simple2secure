package com.simple2secure.api.model;

import java.util.concurrent.TimeUnit;

import com.simple2secure.api.dbo.GenericDBObject;

public class Test extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -914338716345452064L;

	private String podId;
	private String name;
	private String test_content;
	private String hostname;
	private boolean active;
	private boolean scheduled;
	private long scheduledTime;
	private TimeUnit scheduledTimeUnit;
	private long lastScheduleTimestamp;
	private long lastChangedTimestamp;
	private String hash_value;

	public Test() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPodId() {
		return podId;
	}

	public void setPodId(String podId) {
		this.podId = podId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public long getLastScheduleTimestamp() {
		return lastScheduleTimestamp;
	}

	public void setLastScheduleTimestamp(long lastScheduleTimestamp) {
		this.lastScheduleTimestamp = lastScheduleTimestamp;
	}

	public String getTest_content() {
		return test_content;
	}

	public void setTest_content(String test_content) {
		this.test_content = test_content;
	}

	public long getLastChangedTimestamp() {
		return lastChangedTimestamp;
	}

	public void setLastChangedTimestamp(long lastChangedTimestamp) {
		this.lastChangedTimestamp = lastChangedTimestamp;
	}

	public String getHash_value() {
		return hash_value;
	}

	public void setHash_value(String hash_value) {
		this.hash_value = hash_value;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public long getLastExecution() {
		return lastScheduleTimestamp;
	}

	public void setLastExecution(long lastExecution) {
		lastScheduleTimestamp = lastExecution;
	}

}
