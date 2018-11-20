package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "Service")
public class Service extends GenericDBObject {
	private static Logger log = LoggerFactory.getLogger(Service.class);

	private static final long serialVersionUID = 2929933295739730023L;
	private String name;
	private String version;

	protected Service() {
	}

	public Service(String name, String version) {
		super();
		this.name = name;
		this.version = version;
	}

	/**
	 * Returns the name of the service object.
	 *
	 * @return The name as string of this service object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this service object.
	 *
	 * @param name
	 *          The name of the service object.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the version number of this service object.
	 *
	 * @return The version of the service object as string.
	 */
	public String getVersion() {
		return version;
	}

}
