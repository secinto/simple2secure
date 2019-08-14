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
package com.simple2secure.probe.exceptions;

import com.simple2secure.commons.exceptions.BaseException;
import com.simple2secure.commons.messages.Message;

public class ProbeException extends BaseException {

	/**
	 *
	 */
	private static final long serialVersionUID = -7765050829569555584L;

	/**
	 * @param message
	 * @param detailMessage
	 * @param cause
	 */
	public ProbeException(Message message, String detailMessage, Throwable cause) {
		super(message, detailMessage, cause);
	}

	/**
	 * @param message
	 * @param detailMessage
	 */
	public ProbeException(Message message, String detailMessage) {
		super(message, detailMessage);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ProbeException(Message message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ProbeException(Message message) {
		super(message);
	}

}
