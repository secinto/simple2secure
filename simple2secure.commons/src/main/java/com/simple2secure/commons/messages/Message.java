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
package com.simple2secure.commons.messages;

import java.io.Serializable;

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
		return message;
	}
}
