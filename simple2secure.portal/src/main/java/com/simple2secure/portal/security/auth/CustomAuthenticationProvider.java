/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
package com.simple2secure.portal.security.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@Service
@Configurable
public class CustomAuthenticationProvider implements AuthenticationProvider {

	String ROLE_PREFIX = "ROLE_";

	String username;
	String password;

	@Autowired
	UserRepository userRepository;

	public static String userID;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		username = authentication.getPrincipal().toString();
		password = authentication.getCredentials().toString();

		User user = userRepository.findByEmailOnlyActivated(username);

		if (user == null) {
			throw new BadCredentialsException(messageByLocaleService.getMessage("user_with_provided_creds_not_exists"));
		}

		if (passwordEncoder.matches(password, user.getPassword())) {

			userID = user.getId();
			return new UsernamePasswordAuthenticationToken(username, password, getAuthorities(UserRole.LOGINUSER.name()));
		} else {
			throw new BadCredentialsException(messageByLocaleService.getMessage("user_with_provided_creds_not_exists"));
		}

	}

	public static Collection<? extends GrantedAuthority> getAuthorities(String role) {
		List<GrantedAuthority> list = new ArrayList<>();

		list.add(new SimpleGrantedAuthority(role));

		return list;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
