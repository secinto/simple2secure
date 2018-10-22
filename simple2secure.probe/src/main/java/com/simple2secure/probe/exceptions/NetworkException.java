/* Copyright  (c) 2006-2007 Graz University of Technology. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The names "Graz University of Technology" and "IAIK of Graz University of
 *    Technology" must not be used to endorse or promote products derived from
 *    this software without prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE LICENSOR BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 *  OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 *  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY  OF SUCH DAMAGE.
 */
package com.simple2secure.probe.exceptions;

import com.simple2secure.commons.exceptions.BaseException;
import com.simple2secure.commons.messages.Message;
import com.simple2secure.commons.messages.MessageCode;

public class NetworkException extends BaseException {

	/**
	 *
	 */
	private static final long serialVersionUID = -7765050829569555584L;

	/**
	 * @param message
	 * @param detailMessage
	 * @param cause
	 */
	public NetworkException(Message message, String detailMessage, Throwable cause) {
		super(message, detailMessage, cause);
	}

	/**
	 * @param message
	 * @param detailMessage
	 */
	public NetworkException(Message message, String detailMessage) {
		super(message, detailMessage);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NetworkException(Message message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NetworkException(Message message) {
		super(message);
	}

}
