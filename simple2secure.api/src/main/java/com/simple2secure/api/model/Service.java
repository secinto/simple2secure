/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
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
