/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
