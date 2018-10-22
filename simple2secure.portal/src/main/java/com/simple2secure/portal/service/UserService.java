package com.simple2secure.portal.service;

import com.simple2secure.api.model.User;

public interface UserService {

	User findByUsername(String username);

}
