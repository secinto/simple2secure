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
	private CompanyGroup group;
	private boolean activated;

	public Probe() {

	}

	public Probe(String probeId, CompanyGroup group, boolean activated) {
		super();

		this.probeId = probeId;
		this.group = group;
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

	public CompanyGroup getGroup() {
		return group;
	}

	public void setGroup(CompanyGroup group) {
		this.group = group;
	}
}
