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
