package com.simple2secure.api.model;

public class Pod {

	private String podId;
	private CompanyGroup group;
	private boolean activated;
	private String hostname;

	public Pod() {

	}

	public Pod(String podId, CompanyGroup group, boolean activated, String hostname) {
		super();

		this.podId = podId;
		this.group = group;
		this.activated = activated;
		this.hostname = hostname;
	}

	public String getPodId() {
		return podId;
	}

	public void setPodId(String podId) {
		this.podId = podId;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public CompanyGroup getGroup() {
		return group;
	}

	public void setGroup(CompanyGroup group) {
		this.group = group;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}
