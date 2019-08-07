package com.simple2secure.portal.dao.exceptions;

public class ItemNotFoundRepositoryException extends DefaultRepositoryException {

	private static final long serialVersionUID = -6131593834778046682L;
	
	public ItemNotFoundRepositoryException() {
		/*
		 * TODO: Change to working locale
		 */
		super("Item was not found", null);
	}
}
