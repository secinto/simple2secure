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

import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.simple2secure.api.dbo.GenericDBObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ProbePacket")
@Getter
@Setter
@NoArgsConstructor
public class ProbePacket extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -423379436920164346L;
	
	@JsonSerialize(using=ToStringSerializer.class)
	private ObjectId groupId;
	
	private String name;
	private boolean always;
	private int requestCount;
	private long analysisInterval;
	private TimeUnit analysisIntervalUnit;
	private String packetAsHexStream;

	public ProbePacket(ObjectId groupId, String name, boolean always, int requestCount, long analysisInterval, TimeUnit analysisIntervalUnit,
			String packetAsHexStream) {
		this.groupId = groupId;
		this.name = name;
		this.always = always;
		this.requestCount = requestCount;
		this.analysisInterval = analysisInterval;
		this.analysisIntervalUnit = analysisIntervalUnit;
		this.packetAsHexStream = packetAsHexStream;
	}
}
