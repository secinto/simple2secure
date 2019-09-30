package com.simple2secure.api.model;

import java.util.List;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestSequence extends GenericDBObject {

	private static final long serialVersionUID = -914338716345452064L;

	private String podId;
	private String name;
	private List<String> sequenceContent;
	private String sequenceHash;
	private long lastChangedTimeStamp;

	public TestSequence() {
	}

	public TestSequence(String podId, String name, List<String> sequenceContent, String sequenceHash) {
		this.podId = podId;
		this.name = name;
		this.sequenceContent = sequenceContent;
		this.sequenceHash = sequenceHash;
	}

	public String getPodId() {
		return podId;
	}

	public void setPodId(String podId) {
		this.podId = podId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSequenceContent() {
		return sequenceContent;
	}

	public void setSequenceContent(List<String> sequenceContent) {
		this.sequenceContent = sequenceContent;
	}

	public String getSequenceHash() {
		return sequenceHash;
	}

	public void setSequenceHash(String sequenceHash) {
		this.sequenceHash = sequenceHash;
	}

	public long getLastChangedTimeStamp() {
		return lastChangedTimeStamp;
	}

	public void setLastChangedTimeStamp(long lastChangedTimeStamp) {
		this.lastChangedTimeStamp = lastChangedTimeStamp;
	}

}
