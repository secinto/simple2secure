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
