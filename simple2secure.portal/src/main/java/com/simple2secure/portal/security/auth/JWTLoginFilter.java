package com.simple2secure.portal.security.auth;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple2secure.api.model.AccountCredential;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

	TokenAuthenticationService tokenAuthenticationService;

	public JWTLoginFilter(String url, AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		AccountCredential creds = new ObjectMapper().readValue(request.getInputStream(), AccountCredential.class);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword(),
				Collections.emptyList());
		return this.getAuthenticationManager().authenticate(token);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
			throws IOException, ServletException {

		if (tokenAuthenticationService == null) {
			ServletContext servletContext = req.getServletContext();
			WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			tokenAuthenticationService = webApplicationContext.getBean(TokenAuthenticationService.class);
		}

		tokenAuthenticationService.addAuthentication(res, auth.getName(), auth.getAuthorities());
	}
}
