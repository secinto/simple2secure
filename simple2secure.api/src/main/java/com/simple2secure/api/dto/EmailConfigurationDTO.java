package com.simple2secure.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.EmailConfiguration;

public class EmailConfigurationDTO {

	private EmailConfiguration configuration;
	private List<Email> emails = new ArrayList<>();

	public EmailConfigurationDTO() {
	}

	public EmailConfigurationDTO(EmailConfiguration configuration, List<Email> emails) {
		this.configuration = configuration;
		this.emails = emails;
	}

	public EmailConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(EmailConfiguration configuration) {
		this.configuration = configuration;
	}

	public List<Email> getEmails() {
		return emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}
}
