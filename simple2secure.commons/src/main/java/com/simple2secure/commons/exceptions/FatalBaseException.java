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
 * @author dferbas
 *
 */
public class FatalBaseException extends AbstractBaseException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param detailMessage
	 * @param cause
	 */
	public FatalBaseException(Message message, String detailMessage, Throwable cause) {
		super(message, detailMessage, cause);
	}

	/**
	 * @param message
	 * @param detailMessage
	 */
	public FatalBaseException(Message message, String detailMessage) {
		super(message, detailMessage);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FatalBaseException(Message message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public FatalBaseException(Message message) {
		super(message);
	}

}
