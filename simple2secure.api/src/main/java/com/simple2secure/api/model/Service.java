package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "Service")
public class Service extends GenericDBObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2929933295739730023L;
	private String name;
	private String api_url;
	private String backup_location;

	/**
	 *
	 * @param name
	 * @param api_url
	 * @param backup_location
	 */
	
	public Service() {}
	
	public Service(String name, String api_url, String backup_location) {
		super();
		this.name = name;
		this.api_url = api_url;
		this.backup_location = backup_location;
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
	public String getApi_url() {
		return this.api_url;
	}

	/**
	 *
	 * @param api_url
	 */
	public void setApi_url(String api_url) {
		this.api_url = api_url;
	}

	/**
	 *
	 * @return
	 */
	public String getBackup_location() {
		return this.backup_location;
	}

	/**
	 *
	 * @param backup_location
	 */
	public void setBackup_location(String backup_location) {
		this.backup_location = backup_location;
	}
}
