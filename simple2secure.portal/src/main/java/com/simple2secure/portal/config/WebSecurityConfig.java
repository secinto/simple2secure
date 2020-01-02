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

import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.security.CustomEntryPoint;
import com.simple2secure.portal.security.auth.CustomAuthenticationFailureHandler;
import com.simple2secure.portal.security.auth.CustomAuthenticationProvider;
import com.simple2secure.portal.security.auth.CustomAuthenticationSuccessHandler;
import com.simple2secure.portal.security.auth.JWTAuthenticationFilter;
import com.simple2secure.portal.security.auth.JWTLoginFilter;

@Configuration
@EnableWebSecurity
@EnableMongoRepositories("com.simple2secure.portal.dao")
@CrossOrigin(origins = "https://localhost:9000")
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

	@Autowired
	public LoadedConfigItems loadedConfigItems;

	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
		return authProvider;
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers(StaticConfigItems.USER_API + "register/**", StaticConfigItems.USER_API + "/activate/",
				StaticConfigItems.SERVICE_API, StaticConfigItems.TEST_API, StaticConfigItems.USER_API + "/sendResetPasswordEmail",
				StaticConfigItems.USER_API + "/resetPassword/**", StaticConfigItems.USER_API + "/updatePassword/**",
				StaticConfigItems.USER_API + "/invite/**", StaticConfigItems.DOWNLOAD_API + "/**", StaticConfigItems.DEVICE_API + "/**",
				StaticConfigItems.LICENSE_API + "/authenticate/**");
	}

	// TODO - find better solution for antMatchers!
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors().and().csrf().disable().authorizeRequests().antMatchers("/").permitAll().and().authorizeRequests()
				.antMatchers(StaticConfigItems.LOGIN_API).permitAll().and().authorizeRequests()
				.antMatchers(StaticConfigItems.API_ENDPOINT + "/service").permitAll().and().authorizeRequests()
				.antMatchers(StaticConfigItems.API_ENDPOINT + "/register/**").permitAll().and().authorizeRequests()
				.antMatchers(StaticConfigItems.API_ENDPOINT + "/test").permitAll().and().authorizeRequests()
				.antMatchers(StaticConfigItems.API_ENDPOINT + "/download/**").permitAll().and().authorizeRequests()
				.antMatchers(StaticConfigItems.USER_API + "/activate/").permitAll().and().authorizeRequests()
				.antMatchers(StaticConfigItems.USER_API + "/updatePassword/**").permitAll().and().authorizeRequests()
				.antMatchers(StaticConfigItems.USER_API + "/invite/**").permitAll().and().authorizeRequests()
				.antMatchers(StaticConfigItems.USER_API + "/activate/**").permitAll().and().authorizeRequests()
				.antMatchers(StaticConfigItems.USER_API + "/authenticate/**").permitAll().and().authorizeRequests()
				.antMatchers(StaticConfigItems.DEVICE_API + "/**").permitAll().and().authorizeRequests()
				// filter the login requests
				.and()
				.addFilterBefore(new JWTLoginFilter(StaticConfigItems.LOGIN_API, this.authenticationManager()),
						UsernamePasswordAuthenticationFilter.class)
				// And filter other requests to check the presence of JWTth in header
				.addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class).anonymous();

		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
		http.formLogin().successHandler(authenticationSuccessHandler);
		http.formLogin().failureHandler(authenticationFailureHandler);
		// http.requiresChannel().anyRequest().requiresSecure();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider);
	}
}
