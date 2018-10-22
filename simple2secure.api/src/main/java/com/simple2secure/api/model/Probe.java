package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "Probe")
public class Probe extends GenericDBObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1408094765003045124L;
	private String probeId;
	private String groupName;
	private boolean activated;
	
	public Probe() {
		
	}

	public Probe(String probeId, String groupName, boolean activated) {
		super();
		
		this.probeId = probeId;
		this.groupName = groupName;
		this.activated = activated;
	}

	public String getProbeId() {
		return probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
