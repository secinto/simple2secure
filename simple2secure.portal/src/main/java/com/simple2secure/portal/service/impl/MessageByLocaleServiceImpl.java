package com.simple2secure.portal.service.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.simple2secure.portal.service.MessageByLocaleService;

@Service
public class MessageByLocaleServiceImpl implements MessageByLocaleService {

	@Autowired
	private MessageSource messageSource;

	@Override
	public String getMessage(String id) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(id, null, locale);
	}

	@Override
	public String getMessage(String id, String lang) {
		Locale locale = new Locale(lang);
		return messageSource.getMessage(id, null, locale);
	}

	@Override
	public String getMessage(String id, Object[] parameters, String lang) {
		Locale locale = new Locale(lang);
		return messageSource.getMessage(id, parameters, locale);
	}

}
