package com.simple2secure.portal.config;

import java.util.Set;

import org.springframework.security.core.Authentication;

interface PermissionResolver {

	Set<String> resolve(Authentication authentication);
}
