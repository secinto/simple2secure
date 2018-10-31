/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal;

import java.util.Locale;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.utils.DataInitialization;

@EnableScheduling
@SpringBootApplication(scanBasePackages = { "com.simple2secure.portal" }, exclude = { EmbeddedMongoAutoConfiguration.class,
		MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
public class Simple2SecurePortal extends SpringBootServletInitializer {

	@Value("${mail.username}")
	private String mailUser;
	@Value("${mail.password}")
	private String mailPassword;
	@Value("${mail.smtp.auth}")
	private String mailSMTPAuth;
	@Value("${mail.smtp.host}")
	private String mailSMTPHost;
	@Value("${mail.smtp.port}")
	private String mailSMTPPort;

	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailSMTPHost);
		mailSender.setPort(Integer.parseInt(mailSMTPPort));

		mailSender.setUsername(mailUser);
		mailSender.setPassword(mailPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", mailSMTPAuth);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");

		return mailSender;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

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

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Simple2SecurePortal.class, args);
		DataInitialization dataInitializer = context.getBean(DataInitialization.class);
		if (dataInitializer != null) {
			dataInitializer.addDefaultSettings();
			dataInitializer.addDefaultLicensePlan();
		}
	}

}
