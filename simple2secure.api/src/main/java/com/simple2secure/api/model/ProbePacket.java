package com.simple2secure.api.model;

import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "ProbePacket")
public class ProbePacket extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -423379436920164346L;
	private String groupId;
	private String name;
	private boolean always;
	private int requestCount;
	private long analysisInterval;
	private TimeUnit analysisIntervalUnit;
	private String packetAsHexStream;

	private ProbePacket() {
	}

	public ProbePacket(String groupId, String name, boolean always, int requestCount, long analysisInterval, TimeUnit analysisIntervalUnit,
			String packetAsHexStream) {
		this.groupId = groupId;
		this.name = name;
		this.always = always;
		this.requestCount = requestCount;
		this.analysisInterval = analysisInterval;
		this.analysisIntervalUnit = analysisIntervalUnit;
		this.packetAsHexStream = packetAsHexStream;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAlways() {
		return always;
	}

	public void setAlways(boolean always) {
		this.always = always;
	}

	public int getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
	}

	public long getAnalysisInterval() {
		return analysisInterval;
	}

	public void setAnalysisInterval(long analysisInterval) {
		this.analysisInterval = analysisInterval;
	}

	public TimeUnit getAnalysisIntervalUnit() {
		return analysisIntervalUnit;
	}

	public void setAnalysisIntervalUnit(TimeUnit analysisIntervalUnit) {
		this.analysisIntervalUnit = analysisIntervalUnit;
	}

	public String getPacketAsHexStream() {
		return packetAsHexStream;
	}

	public void setPacketAsHexStream(String packetAsHexStream) {
		this.packetAsHexStream = packetAsHexStream;
	}

}
