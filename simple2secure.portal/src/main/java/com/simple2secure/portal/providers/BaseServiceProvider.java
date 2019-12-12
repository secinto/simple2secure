package com.simple2secure.portal.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.security.PasswordValidator;
import com.simple2secure.portal.security.auth.TokenAuthenticationService;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.DataInitialization;

public class BaseServiceProvider extends BaseRepositoryProvider {
	/*
	 * Special services
	 */

	@Autowired
	public MessageByLocaleService messageByLocaleService;

	@Autowired
	public LoadedConfigItems loadedConfigItems;

	@Autowired
	public DataInitialization dataInitialization;

	@Autowired
	public ErrorAttributes errorAttributes;

	@Autowired
	public TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	public PasswordEncoder passwordEncoder;

	@Autowired
	public PasswordValidator passwordValidator;
}
