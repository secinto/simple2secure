package com.simple2secure.api.dto;

import com.simple2secure.api.model.Notification;

public class NotificationDTO {

	Notification notification;

	TestStatusDTO testRunDTO;

	public NotificationDTO() {
	}

	public NotificationDTO(Notification notification, TestStatusDTO testRunDTO) {
		this.notification = notification;
		this.testRunDTO = testRunDTO;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public TestStatusDTO getTestRunDTO() {
		return testRunDTO;
	}

	public void setTestRunDTO(TestStatusDTO testRunDTO) {
		this.testRunDTO = testRunDTO;
	}

}
