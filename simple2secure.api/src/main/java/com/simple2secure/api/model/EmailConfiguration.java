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

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.simple2secure.api.dbo.GenericDBObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailConfiguration extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1616161487161004710L;

	@JsonSerialize(
			using = ToStringSerializer.class)
	private ObjectId contextId;

	private String incomingServer;
	private String incomingPort;
	private String outgoingServer;
	private String outgoingPort;
	private String email;
	private String password;
	private int lastEnd;

	private Status currentStatus;

	public EmailConfiguration(ObjectId contextId, String incomingServer, String incomingPort, String outgoingServer, String outgoingPort,
			String email, String password) {
		this();

		this.contextId = contextId;
		this.incomingServer = incomingServer;
		this.incomingPort = incomingPort;
		this.outgoingServer = outgoingServer;
		this.outgoingPort = outgoingPort;
		this.email = email;
		this.password = password;
	}
}
