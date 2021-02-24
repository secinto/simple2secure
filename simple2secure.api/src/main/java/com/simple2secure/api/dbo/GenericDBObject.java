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
package com.simple2secure.api.dbo;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@MappedSuperclass
public abstract class GenericDBObject implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7464953476214229002L;

	public static String ID = "_id";

	@EmbeddedId
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using=ToStringSerializer.class)
	public ObjectId id;

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	@JsonSerialize(using=ToStringSerializer.class)
	public ObjectId getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			return id.equals(((GenericDBObject) obj).getId());
		} catch (Exception ex) {
			return false;
		}
	}
}
