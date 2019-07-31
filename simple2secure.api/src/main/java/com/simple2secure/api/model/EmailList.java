package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class EmailList extends GenericDBObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -2208957747330744059L;

	private String email;
	private EmailListEnum type;

	public EmailList(String email, EmailListEnum type) {
		this.email = email;
		this.type = type;
	}

	public EmailList() {

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public EmailListEnum getType() {
		return type;
	}

	public void setType(EmailListEnum type) {
		this.type = type;
	}

}
