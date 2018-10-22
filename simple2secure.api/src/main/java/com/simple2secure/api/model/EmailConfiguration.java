package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class EmailConfiguration extends GenericDBObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1616161487161004710L;
	
	private String userUUID;
	private String incomingServer;
	private String incomingPort;
	private String outgoingServer;
	private String outgoingPort;
	private String email;
	private String password;
	
	public EmailConfiguration() {
		
	}
	
	public EmailConfiguration(String userUUID, String incomingServer, String incomingPort, String outgoingServer, String outgoingPort, String email, String password) {
		this.userUUID = userUUID;
		this.incomingServer = incomingServer;
		this.incomingPort = incomingPort;
		this.outgoingServer = outgoingServer;
		this.outgoingPort = outgoingPort;
		this.email = email;
		this.password = password;
	}

	public String getUserUUID() {
		return userUUID;
	}

	public void setUserUUID(String userUUID) {
		this.userUUID = userUUID;
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
