package com.simple2secure.api.model;

import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
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

	private String probeId;
	@ElementCollection
	@MapKeyColumn(name = "key")
	@Column(name = "value")
	private Map<String, String> content;
	private String startTime;
	private String processorName;
	private boolean sent;

	public NetworkReport() {
		content = new TreeMap<String, String>();
	}

	public NetworkReport(String probeId, Map<String, String> content, String startTime, boolean sent) {
		this.probeId = probeId;
		this.content = content;
		this.startTime = startTime;
		this.sent = sent;
	}

	public String getProbeId() {
		return probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	public void addContent(String key, String value) {
		content.put(key, value);
	}

	public Map<String, String> getContent() {
		return content;
	}

	public void setContent(Map<String, String> content) {
		this.content = content;
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
}
