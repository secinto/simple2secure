package com.simple2secure.portal.service;

public interface MessageByLocaleService {
	public String getMessage(String id);

	public String getMessage(String id, String lang);

	public String getMessage(String id, Object[] parameters, String lang);

}
