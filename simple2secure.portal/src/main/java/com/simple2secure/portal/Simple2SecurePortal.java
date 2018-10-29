/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.simple2secure.commons.config.LoadedConfigItems;

@EnableScheduling
@SpringBootApplication(scanBasePackages = { "com.simple2secure.portal" }, exclude = {
		EmbeddedMongoAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
public class Simple2SecurePortal extends SpringBootServletInitializer {


	@Bean
	public SessionLocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.ENGLISH);
		return slr;
	}

	@Bean
	public LoadedConfigItems loadedConfigItems() {
		LoadedConfigItems configItems = new LoadedConfigItems();
		configItems.init();
		return configItems;
	}

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:locale/messages");
		messageSource.setCacheSeconds(3600); // refresh cache once per hour
		return messageSource;
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Simple2SecurePortal.class);
	}

	/**
	 * This function initiates http on port 8080 but redirects all trafic to https
	 *
	 * @return
	 */
	// @Bean
	// public ServletWebServerFactory servletContainer(@Value("${server.http.port}")
	// int httpPort) {
	// Connector connector = new
	// Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
	// connector.setPort(httpPort);
	// connector.setRedirectPort(8443);
	//
	// TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
	// @Override
	// protected void postProcessContext(Context context) {
	// SecurityConstraint securityConstraint = new SecurityConstraint();
	// securityConstraint.setUserConstraint("CONFIDENTIAL");
	// SecurityCollection collection = new SecurityCollection();
	// collection.addPattern("/*");
	// securityConstraint.addCollection(collection);
	// context.addConstraint(securityConstraint);
	// }
	// };
	//
	// tomcat.addAdditionalTomcatConnectors(connector);
	//
	// return tomcat;
	// }

	public static void main(String[] args) {
		SpringApplication.run(Simple2SecurePortal.class, args);
	}

}
