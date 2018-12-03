package com.simple2secure.portal.security.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleCheckInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		try {

			/*
			 * String currentUser = auth.getPrincipal().toString();
			 *
			 * String username = current.getUser().getUsername(); if (users_to_update_roles.contains(username)) { updateRoles(auth, current);
			 * users_to_update_roles.remove(username); }
			 */

		} catch (Exception e) {
			// TODO: handle exception
		}

		return true;
	}

}
