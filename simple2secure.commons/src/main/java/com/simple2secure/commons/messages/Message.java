package com.simple2secure.commons.messages;

import java.io.Serializable;
import java.util.Locale;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String messageTag;
	private final String message;

	/**
	 * Create new holder for a message without params.
	 *
	 * @param messageCode
	 *
	 */
	public Message(String messageTag) {
		this(messageTag, null);
	}

	/**
	 * Create new holder for a message with params.
	 *
	 * @param messageCode
	 * @param messageParams
	 *          may be null or empty
	 */
	public Message(String messageTag, String message) {
		this.messageTag = messageTag;
		this.message = message;
	}

	/**
	 * @return the detailMessage
	 */
	public String getMessage() {
		return this.message;
	}
}
