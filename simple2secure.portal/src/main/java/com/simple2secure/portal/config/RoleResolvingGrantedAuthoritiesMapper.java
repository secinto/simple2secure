package com.simple2secure.portal.config;

import java.util.Collection;

import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

public class RoleResolvingGrantedAuthoritiesMapper extends RoleHierarchyAuthoritiesMapper {
	private final GrantedAuthoritiesMapper delegate;

	public RoleResolvingGrantedAuthoritiesMapper(RoleHierarchy roleHierarchy, GrantedAuthoritiesMapper delegate) {
		super(roleHierarchy);
		this.delegate = delegate;
	}

	@Override
	public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {

		// Transform roles if necessary
		Collection<? extends GrantedAuthority> transformedAuthorities = delegate.mapAuthorities(authorities);

		// Roles resolved via role hierarchy
		Collection<? extends GrantedAuthority> expanededAuthorities = super.mapAuthorities(transformedAuthorities);

		return expanededAuthorities;
	}

}
