package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;


public class DeviceInfo extends GenericDBObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7168259292146418922L;
	
	protected String deviceId;
	protected String hostName;
	protected String ipAddress;
	protected String netMask;
	protected DeviceStatus deviceStatus = DeviceStatus.UNKNOWN;
	protected long lastOnlineTimestamp;
	
	public DeviceInfo() {}
	
	public DeviceInfo(String deviceId, String hostName, String ipAddress, String netMask, DeviceStatus deviceStatus) {
		setDeviceId(deviceId);
		setHostName(hostName);
		setIpAddress(ipAddress);
		setNetMask(netMask);
		setDeviceStatus(deviceStatus);
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
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

	public DeviceStatus getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(DeviceStatus deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public long getLastOnlineTimestamp() {
		return lastOnlineTimestamp;
	}

	public void setLastOnlineTimestamp(long lastOnlineTimestamp) {
		this.lastOnlineTimestamp = lastOnlineTimestamp;
	}
}
