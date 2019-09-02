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
package com.simple2secure.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.simple2secure.portal.security.CustomEntryPoint;
import com.simple2secure.portal.security.auth.CustomAuthenticationFailureHandler;
import com.simple2secure.portal.security.auth.CustomAuthenticationProvider;
import com.simple2secure.portal.security.auth.CustomAuthenticationSuccessHandler;
import com.simple2secure.portal.security.auth.JWTAuthenticationFilter;
import com.simple2secure.portal.security.auth.JWTLoginFilter;

@Configuration
@EnableWebSecurity
@EnableMongoRepositories("com.simple2secure.portal.dao")
@CrossOrigin(origins = "http://localhost:9000")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	CustomAuthenticationProvider authProvider;

	@Autowired
	private CustomEntryPoint authenticationEntryPoint;

	@Autowired
	private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	private CustomAuthenticationFailureHandler authenticationFailureHandler;

	@Autowired
	private UserDetailsService userDetailsService;

	private String[] antmatchers = { "/config/**", "/api/register/**", "/api/user/activate/", "/api/service/**", "/api/test",
			"/api/user/sendResetPasswordEmail", "/api/user/resetPassword/**", "/api/user/updatePassword/**", "/api/user/invite/**",
			"/api/download/**", "/api/device/**", "/api/license/activateProbe", "/api/license/activatePod/**", "/api/pod/config/**" };

	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
		return authProvider;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/config/**", "/api/register/**", "/api/user/activate/", "/api/service/**", "/api/test",
				"/api/user/sendResetPasswordEmail", "/api/user/resetPassword/**", "/api/user/updatePassword/**", "/api/user/invite/**",
				"/api/download/**", "/api/device/**", "/api/license/activateProbe", "/api/license/activatePod/**", "/api/pod/config/**");
	}

	// TODO - find better solution for antMatchers!
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors().and().csrf().disable().authorizeRequests().antMatchers("/").permitAll().antMatchers("/api/login").permitAll()
				.antMatchers("/api/service/").permitAll().antMatchers("/api/register/**").anonymous().and().authorizeRequests()
				.antMatchers("/api/user/activate/").anonymous().and().authorizeRequests().antMatchers("/api/test").anonymous().and()
				.authorizeRequests().antMatchers("/api/user/sendResetPasswordEmail").anonymous().and().authorizeRequests()
				.antMatchers("/api/user/resetPassword/**").anonymous().and().authorizeRequests().antMatchers("/api/user/updatePassword/**")
				.anonymous().and().authorizeRequests().antMatchers("/api/user/invite/**").anonymous().and().authorizeRequests()
				.antMatchers("/api/download/**").anonymous().and().authorizeRequests().antMatchers("/api/device/**").anonymous().and()
				.authorizeRequests().antMatchers("/api/license/activateProbe").anonymous().and().authorizeRequests()
				.antMatchers("/api/license/activatePod/**").anonymous().and().authorizeRequests().antMatchers("/api/pod/config/**").anonymous()
				.and().authorizeRequests().and()
				// We filter the api/login requests
				.addFilterBefore(new JWTLoginFilter("/api/login", this.authenticationManager()), UsernamePasswordAuthenticationFilter.class)
				// And filter other requests to check the presence of JWTth in header
				.addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class).anonymous();

		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
		http.formLogin().successHandler(authenticationSuccessHandler);
		http.formLogin().failureHandler(authenticationFailureHandler);
		// http.requiresChannel().anyRequest().requiresSecure();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// auth.authenticationProvider(authProvider());
		// auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(authProvider);
	}
}
