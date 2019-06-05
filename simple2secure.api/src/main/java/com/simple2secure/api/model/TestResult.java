package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestResult extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -6306698374548193553L;

	private String name;
	private String licenseId;
	private String groupId;
	private String hostname;
	private TestResultObj result;
	private long timestamp;

	public TestResult() {

	}

	public TestResult(String name, String licenseId, String groupId, String hostname, TestResultObj result, long timestamp) {
		this.name = name;
		this.licenseId = licenseId;
		this.groupId = groupId;
		this.hostname = hostname;
		this.result = result;
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TestResultObj getResult() {
		return result;
	}

	public void setResult(TestResultObj result) {
		this.result = result;
	}

	public String getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(String licenseId) {
		this.licenseId = licenseId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}
