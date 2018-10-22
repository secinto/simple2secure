package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "Step")
public class Step extends GenericDBObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -213373299338629068L;
	private String probeId;
	private String groupId;
	private int number;
	private String name;
	private int active;
	private boolean isGroupStep;

	public Step() {

	}

	/**
	 *
	 * @param number
	 * @param name
	 */
	public Step(String probeId, int number, String name, int active) {
		super();
		this.probeId = probeId;
		this.number = number;
		this.name = name;
		this.active = active;
	}

	public String getProbeId() {
		return probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 *
	 * @return
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 *
	 * @param number
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 *
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @return
	 */
	public int getActive() {
		return this.active;
	}

	/**
	 *
	 * @param active
	 */
	public void setActive(int active) {
		this.active = active;
	}

	public boolean isGroupStep() {
		return isGroupStep;
	}

	public void setGroupStep(boolean isGroupStep) {
		this.isGroupStep = isGroupStep;
	}
}
