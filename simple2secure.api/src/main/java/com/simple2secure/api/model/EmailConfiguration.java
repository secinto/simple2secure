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

	public EmailConfiguration() {

	}

	public EmailConfiguration(String contextId, String incomingServer, String incomingPort, String outgoingServer, String outgoingPort,
			String email, String password) {
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
}
