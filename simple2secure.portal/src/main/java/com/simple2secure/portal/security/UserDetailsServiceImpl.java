package com.simple2secure.portal.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.simple2secure.api.model.User;
import com.simple2secure.portal.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	// public UserDetailsServiceImpl(UserRepository userRepository) {
	// this.userRepository = userRepository;
	// }
	//
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException(username);
		}

		// List<GrantedAuthority> authorities = buildUserAuthority(user.getUserRole());

		return buildUserForAuthentication(user, null);
	}

	private org.springframework.security.core.userdetails.User buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
		// user.setEnabled(true);
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.isEnabled(), true, true,
				true, authorities);
	}

	private List<GrantedAuthority> buildUserAuthority(String userRole) {

		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

		setAuths.add(new SimpleGrantedAuthority(userRole));

		List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);

		return Result;
	}
}
