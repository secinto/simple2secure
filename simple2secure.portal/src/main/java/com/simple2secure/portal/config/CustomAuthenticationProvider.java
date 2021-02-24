package com.simple2secure.portal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.CurrentContext;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.CurrentContextRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider extends KeycloakAuthenticationProvider {
	private final GrantedAuthoritiesMapper grantedAuthoritiesMapper;

	@Autowired
	CurrentContextRepository currentContextRepo;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepo;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;

		Collection<? extends GrantedAuthority> keycloakAuthorities = mapAuthorities(addKeycloakRoles(token));
		Collection<? extends GrantedAuthority> grantedAuthorities = addUserSpecificAuthorities(authentication, keycloakAuthorities);

		return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), grantedAuthorities);
	}

	protected Collection<? extends GrantedAuthority> addUserSpecificAuthorities(Authentication authentication,
			Collection<? extends GrantedAuthority> authorities) {

		List<GrantedAuthority> result = new ArrayList<>();
		result.addAll(authorities);

		CurrentContext userContext = currentContextRepo.findByUserId(authentication.getName());

		if (userContext != null) {
			ContextUserAuthentication userAuth = contextUserAuthRepo.find(userContext.getContextUserAuthenticationId());
			if (userAuth != null) {
				result.add(new SimpleGrantedAuthority(userAuth.getUserRole().name()));
			}

		}

		return result;
	}

	protected Collection<? extends GrantedAuthority> addKeycloakRoles(KeycloakAuthenticationToken token) {

		Collection<GrantedAuthority> keycloakRoles = new ArrayList<>();

		for (String role : token.getAccount().getRoles()) {
			keycloakRoles.add(new KeycloakRole(role));
		}

		return keycloakRoles;
	}

	private Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
		return grantedAuthoritiesMapper != null ? grantedAuthoritiesMapper.mapAuthorities(authorities) : authorities;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return KeycloakAuthenticationToken.class.isAssignableFrom(aClass);
	}
}
