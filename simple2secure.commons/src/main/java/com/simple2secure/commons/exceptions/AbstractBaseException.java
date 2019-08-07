/**
 *********************************************************************
 *
 * Copyright (C) 2019 by secinto GmbH (http://www.secinto.com)
 *
 *********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 *********************************************************************
 */
package com.simple2secure.commons.exceptions;

import com.simple2secure.commons.messages.Message;

/**
 * Abstract class which is used as the base of the Secinto exceptions. e.g. {@link BaseException}
 *
 * @author skraxberger
 */
public abstract class AbstractBaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Message message;
	private String detailMessage;

	/**
	 * @param message
	 */
	public AbstractBaseException(Message message) {
		super(createMessage(message));
		this.message = message;
	}

	/**
	 * @param message
	 * @param detailMessage
	 */
	public AbstractBaseException(Message message, String detailMessage) {
		super(createMessage(message, detailMessage));
		this.message = message;
		this.detailMessage = detailMessage;
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public AbstractBaseException(Message message, Throwable cause) {
		super(createMessage(message), cause);
		this.message = message;
	}

	/**
	 *
	 * @param message
	 * @param detailMessage
	 * @param cause
	 */
	public AbstractBaseException(Message message, String detailMessage, Throwable cause) {
		super(createMessage(message, detailMessage), cause);
		this.message = message;
		this.detailMessage = detailMessage;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return message.getMessage();
	}

	/**
	 * @return the detailMessage
	 */
	public String getDetailMessage() {
		return detailMessage;
	}

	/**
	 * @param detailMessage
	 *          the detailMessage to set
	 */
	public void setDetailMessage(String detailMessage) {
		this.detailMessage = detailMessage;
	}

	/**
	 * Formats the message contained in the provided {@link Message} by appending the message code with a semicolon and the message
	 * description.
	 *
	 * @param message
	 *          The message object which should be formated and returned as String
	 * @return
	 */
	private static String createMessage(Message message) {
		return message.getMessage();
	}

	/**
	 * Formats the message contained in the provided {@link Message} and the detailed description of the message from parameter
	 * <code>detailMessage</code> based on {@link #createMessage(Message)} and appending the detailed message between square brackets.
	 *
	 * @param message
	 *          The message object which should be formated and returned as String
	 * @param detailMessage
	 *          The detailed description which should be added to the message
	 * @return
	 */
	private static String createMessage(Message message, String detailMessage) {
		return createMessage(message) + " [" + detailMessage + "] "; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
