package com.simple2secure.portal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simple2secure.api.model.User;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User findByUsername(String username) {
		return userRepository.findByEmailOnlyActivated(username);
	}
}
