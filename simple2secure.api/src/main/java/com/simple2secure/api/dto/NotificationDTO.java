package com.simple2secure.api.dto;

import com.simple2secure.api.model.Notification;

public class NotificationDTO {

	Notification notification;

	TestRunDTO testRunDTO;

	public NotificationDTO() {
	}

	public NotificationDTO(Notification notification, TestRunDTO testRunDTO) {
		this.notification = notification;
		this.testRunDTO = testRunDTO;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public TestRunDTO getTestRunDTO() {
		return testRunDTO;
	}

	public void setTestRunDTO(TestRunDTO testRunDTO) {
		this.testRunDTO = testRunDTO;
	}

}
