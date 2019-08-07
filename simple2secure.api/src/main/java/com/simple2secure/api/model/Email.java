package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Email extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -6339585681852758358L;

	private String messageId;
	private String configId;
	private int number;
	private String subject;
	private String from;
	private String text;
	private String receivedDate;

	public Email() {

	}

	public Email(String messageId, String configId, int number, String subject, String from, String text, String receivedDate) {
		this.configId = configId;
		this.number = number;
		this.subject = subject;
		this.from = from;
		this.text = text;
		this.receivedDate = receivedDate;
		this.messageId = messageId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getConfigId() {
		return configId;
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
