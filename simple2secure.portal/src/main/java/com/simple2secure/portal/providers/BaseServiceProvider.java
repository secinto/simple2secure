package com.simple2secure.portal.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.security.auth.DeviceAuthenticationService;
import com.simple2secure.portal.security.auth.KeycloakAuthenticationService;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.DataInitialization;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	public DeviceAuthenticationService tokenAuthenticationService;

	@Autowired
	public KeycloakAuthenticationService keycloakAuthenticationService;

	@Autowired
	public PasswordEncoder passwordEncoder;
}
