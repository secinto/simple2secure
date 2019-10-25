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

import com.simple2secure.api.dbo.GenericDBObject;

public class EmailConfiguration extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1616161487161004710L;

	private String contextId;
	private String incomingServer;
	private String incomingPort;
	private String outgoingServer;
	private String outgoingPort;
	private String email;
	private String password;
	private int lastEnd;

	private Status currentStatus;

	public EmailConfiguration() {
		currentStatus = Status.CREATED;
		lastEnd = 0;
	}

	public EmailConfiguration(String contextId, String incomingServer, String incomingPort, String outgoingServer, String outgoingPort,
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

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getIncomingServer() {
		return incomingServer;
	}

	public void setIncomingServer(String incomingServer) {
		this.incomingServer = incomingServer;
	}

	public String getIncomingPort() {
		return incomingPort;
	}

	public void setIncomingPort(String incomingPort) {
		this.incomingPort = incomingPort;
	}

	public String getOutgoingServer() {
		return outgoingServer;
	}

	public void setOutgoingServer(String outgoingServer) {
		this.outgoingServer = outgoingServer;
	}

	public String getOutgoingPort() {
		return outgoingPort;
	}

	public void setOutgoingPort(String outgoingPort) {
		this.outgoingPort = outgoingPort;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Status getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(Status currentStatus) {
		this.currentStatus = currentStatus;
	}

	public int getLastEnd() {
		return lastEnd;
	}

	public void setLastEnd(int lastEnd) {
		this.lastEnd = lastEnd;
	}

}
