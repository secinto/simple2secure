package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Email extends GenericDBObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6339585681852758358L;
	
	private String messageID;
	private String userUUID;
	private String configID;
	private int number;
	private String subject;
	private String from;
	private String text;
	private String receivedDate;
	
	public Email(String messageID, String userUUID, String configID, int number, String subject, String from, String text, String receivedDate) {
		this.userUUID = userUUID;
		this.configID = configID;
		this.number = number;
		this.subject = subject;
		this.from = from;
		this.text = text;
		this.receivedDate = receivedDate;
		this.messageID = messageID;
	}
		
	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getUserUUID() {
		return userUUID;
	}

	public void setUserUUID(String userUUID) {
		this.userUUID = userUUID;
	}
	
	public String getConfigID() {
		return configID;
	}

	public void setConfigID(String configID) {
		this.configID = configID;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}
	
}
