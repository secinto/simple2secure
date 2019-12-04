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

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "Step")
public class Step extends GenericDBObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -213373299338629068L;
	private int number;
	private String name;
	private int active;

	public Step() {

	}

	/**
	 *
	 * @param number
	 * @param name
	 */
	public Step(int number, String name, int active) {
		super();
		this.number = number;
		this.name = name;
		this.active = active;
	}

	/**
	 *
	 * @return
	 */
	public int getNumber() {
		return number;
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
		return name;
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
		return active;
	}

	/**
	 *
	 * @param active
	 */
	public void setActive(int active) {
		this.active = active;
	}
}
