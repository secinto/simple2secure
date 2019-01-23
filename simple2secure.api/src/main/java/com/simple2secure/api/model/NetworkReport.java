package com.simple2secure.api.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "NetworkReport")
public class NetworkReport extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -5984944130903360444L;
	private String groupId;
	private String probeId;

	@ElementCollection
	@MapKeyColumn(name = "key")
	@Column(name = "value")
	private Map<String, String> ipPair;

	@Lob
	private String stringContent;

	private String startTime;
	private String processorName;
	private boolean sent;

	public NetworkReport() {
		// content = new TreeMap<String, String>();
	}

	public NetworkReport(String probeId, String content, String startTime, Map<String, String> ipPair, boolean sent) {
		this.probeId = probeId;
		// this.content = content;
		this.startTime = startTime;
		this.sent = sent;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getProbeId() {
		return probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	public String getStringContent() {
		return stringContent;
	}

	public void setStringContent(String stringContent) {
		this.stringContent = stringContent;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public String getProcessorName() {
		return processorName;
	}

	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	public Map<String, String> getIpPair() {
		return ipPair;
	}

	public void setIpPair(Map<String, String> ipPair) {
		this.ipPair = ipPair;
	}
}
