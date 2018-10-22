/**
 *
 */
package com.simple2secure.commons.exceptions;

import com.simple2secure.commons.messages.Message;
import com.simple2secure.commons.messages.MessageCode;

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
