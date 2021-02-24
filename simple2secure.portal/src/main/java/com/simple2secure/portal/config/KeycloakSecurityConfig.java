package com.simple2secure.portal.config;

import java.util.Arrays;

import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import com.simple2secure.api.model.UserRole;
import com.simple2secure.portal.utils.PortalUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableConfigurationProperties({ KeycloakSpringBootProperties.class, SecurityPropertiesExtension.class })
@RequiredArgsConstructor
@KeycloakConfiguration
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(keycloakAuthenticationProvider());
	}

	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	@Bean
	@Override
	protected KeycloakAuthenticationProvider keycloakAuthenticationProvider() {

		SimpleAuthorityMapper grantedAuthorityMapper = new SimpleAuthorityMapper();
		// grantedAuthorityMapper.setPrefix("ROLE_");
		grantedAuthorityMapper.setConvertToUpperCase(true);

		RoleResolvingGrantedAuthoritiesMapper resolvingMapper = new RoleResolvingGrantedAuthoritiesMapper(roleHierarchy(),
				grantedAuthoritiesMapper());
		// RoleAppendingGrantedAuthoritiesMapper
		return new CustomAuthenticationProvider(resolvingMapper);
	}

	@Bean
	public GrantedAuthoritiesMapper grantedAuthoritiesMapper()

	{
		SimpleAuthorityMapper grantedAuthorityMapper = new SimpleAuthorityMapper();
		// grantedAuthorityMapper.setPrefix("ROLE_");
		grantedAuthorityMapper.setConvertToUpperCase(true);
		return grantedAuthorityMapper;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		String[] unsecuredApis = portalUtils.getListOfNotSecuredApis(getApplicationContext());

		log.info("Following APIs are annotated as insecured {}", Arrays.toString(unsecuredApis));

		super.configure(http);
		http.cors().and().csrf().disable().authorizeRequests().antMatchers(unsecuredApis).permitAll();
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl rhi = new RoleHierarchyImpl();
		rhi.setHierarchy(UserRole.SUPERADMIN + " > " + UserRole.ADMIN + " > " + UserRole.SUPERUSER + " > " + UserRole.USER + " > "
				+ UserRole.LOGINUSER + " > " + UserRole.DEVICE);
		return rhi;
	}
}
