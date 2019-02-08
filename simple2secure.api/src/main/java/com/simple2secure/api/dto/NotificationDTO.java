package com.simple2secure.api.dto;

import com.simple2secure.api.model.Notification;
import com.simple2secure.api.model.Tool;

public class NotificationDTO extends Notification {

	/**
	 *
	 */
	private static final long serialVersionUID = -4016184022840808308L;
	private Tool tool;

	public NotificationDTO(Tool tool, Notification notification) {
		super(notification.getContextId(), notification.getToolId(), notification.getName(), notification.getContent(),
				notification.getTimestamp(), notification.isRead());
		this.tool = tool;
	}

	public Tool getTool() {
		return tool;
	}

	public void setTool(Tool tool) {
		this.tool = tool;
	}

}
