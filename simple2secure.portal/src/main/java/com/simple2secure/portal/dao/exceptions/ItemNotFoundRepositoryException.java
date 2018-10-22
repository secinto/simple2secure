package com.simple2secure.portal.dao.exceptions;

import com.simple2secure.commons.messages.MessageCodeUtil;

public class ItemNotFoundRepositoryException extends DefaultRepositoryException {

	private static final long serialVersionUID = -6131593834778046682L;

	public ItemNotFoundRepositoryException() {
		super(MessageCodeUtil.getMessageCodeMessage(null), null);
	}
}
