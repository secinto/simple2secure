package com.simple2secure.portal.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.security.CustomEntryPoint;
import com.simple2secure.portal.security.auth.CustomAuthenticationFailureHandler;
import com.simple2secure.portal.security.auth.CustomAuthenticationProvider;
import com.simple2secure.portal.security.auth.CustomAuthenticationSuccessHandler;
import com.simple2secure.portal.security.auth.JWTAuthenticationFilter;
import com.simple2secure.portal.security.auth.JWTLoginFilter;
import com.simple2secure.portal.utils.PortalUtils;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMongoRepositories("com.simple2secure.portal.dao")
@CrossOrigin(origins = "https://localhost:9000")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
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

	@Autowired
	public LoadedConfigItems loadedConfigItems;

	@Autowired
	private PortalUtils portalUtils;

	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
		return authProvider;
	}

	/**
	 * Annotate the function with @NotSecuredApi if you want to exclude the api from the autorization
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		String[] unsecuredApis = portalUtils.getListOfNotSecuredApis(getApplicationContext());

		log.info("Following APIs are annotated as insecured {}", Arrays.toString(unsecuredApis));

		http.cors().and().csrf().disable().authorizeRequests().antMatchers(unsecuredApis).permitAll().and().authorizeRequests().anyRequest()
				.authenticated().and()
				.addFilterBefore(new JWTLoginFilter(StaticConfigItems.LOGIN_API, this.authenticationManager()),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class).anonymous();

		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
		http.formLogin().successHandler(authenticationSuccessHandler);
		http.formLogin().failureHandler(authenticationFailureHandler);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider);
	}
}
