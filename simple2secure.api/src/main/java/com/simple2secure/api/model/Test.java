package com.simple2secure.api.model;

import java.util.concurrent.TimeUnit;

import com.simple2secure.api.dbo.GenericDBObject;

public class Test extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -914338716345452064L;

	private String podId;
	private String contextId;
	private String hostname;
	private String description;
	private String version;
	private TestStep precondition;
	private TestStep step;
	private TestStep postcondition;
	private boolean active;
	private boolean scheduled;
	private long scheduledTime;
	private TimeUnit scheduledTimeUnit;
	private long lastScheduleTimestamp;

	public Test() {

	}

	public String getPodId() {
		return podId;
	}

	public void setPodId(String podId) {
		this.podId = podId;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public long getLastScheduleTimestamp() {
		return lastScheduleTimestamp;
	}

	public void setLastScheduleTimestamp(long lastScheduleTimestamp) {
		this.lastScheduleTimestamp = lastScheduleTimestamp;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public TestStep getPrecondition() {
		return precondition;
	}

	public void setPrecondition(TestStep precondition) {
		this.precondition = precondition;
	}

	public TestStep getStep() {
		return step;
	}

	public void setStep(TestStep step) {
		this.step = step;
	}

	public TestStep getPostcondition() {
		return postcondition;
	}

	public void setPostcondition(TestStep postcondition) {
		this.postcondition = postcondition;
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
