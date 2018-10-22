package com.simple2secure.portal.exceptions;

public class ItemNotFoundRepositoryException extends DefaultRepositoryException {

	private static final long serialVersionUID = -6131593834778046682L;

	public ItemNotFoundRepositoryException() {
		super(/*MessageCodeUtil.getMessageCodeMessage(MessageCodeGeneral.item_not_found)*/"item_not_found", null); // $NON-NLS-1$
	}
}
