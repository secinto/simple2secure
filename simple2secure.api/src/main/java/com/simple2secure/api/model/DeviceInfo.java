package com.simple2secure.api.model;

import javax.persistence.Embeddable;

@Embeddable
public class DeviceInfo {
	
	private String hostname;
	private String ipAddress;
	private String netMask;
	
	public DeviceInfo() {}
	
	public DeviceInfo(String hostname, String ipAddress, String netMask) {
		super();
		setHostname(hostname);
		setIpAddress(ipAddress);
		setNetMask(netMask);
	}

	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getNetMask() {
		return netMask;
	}
	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}
}
