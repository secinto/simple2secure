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
			"/api/user/sendResetPasswordEmail", "/api/users/resetPassword/**", "/api/user/updatePassword/**", "/api/user/invite/**",
			"/api/download/**", "/api/device/**", "/api/license/activateProbe", "/api/license/activatePod/**" };

	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
		return authProvider;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(antmatchers);
	}

	// TODO - find better solution for antMatchers!
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors().and().csrf().disable().authorizeRequests().antMatchers("/").permitAll().antMatchers("/api/login").permitAll()
				.antMatchers("/api/service/").permitAll().antMatchers(antmatchers).anonymous().and().authorizeRequests().and()
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
