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
package com.simple2secure.portal;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.ServiceLibraryRepository;

@EnableScheduling
@SpringBootApplication(scanBasePackages = { "com.simple2secure.portal" }, exclude = { EmbeddedMongoAutoConfiguration.class,
		MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
public class Simple2SecurePortal extends SpringBootServletInitializer {

	private static Logger log = LoggerFactory.getLogger(Simple2SecurePortal.class);

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

	@Autowired
	private ServiceLibraryRepository serviceLibraryRepository;

	@Autowired
	private Environment env;

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
		// props.put("mail.smtp.socketFactory.port", mailSMTPPort);
		// props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// props.put("mail.smtp.socketFactory.fallback", "false");

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
		return LoadedConfigItems.getInstance();
	}

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:locale/messages");
		messageSource.setCacheSeconds(3600); // refresh cache once per hour
		return messageSource;
	}

	@PostConstruct
	public void initialize() throws IOException {
		// ClassLoader classLoader = getClass().getClassLoader();
		// File file = new File(classLoader.getResource("probe/simple2secure.probe-0.1.0.jar").getFile());
		// ServiceLibrary library = new ServiceLibrary("Probe", "0.1.0", file.getAbsolutePath());
		// serviceLibraryRepository.save(library);
		// file = new File(classLoader.getResource("probe/simple2secure.probe-0.1.1.jar").getFile());
		// library = new ServiceLibrary("Probe", "0.1.1", file.getAbsolutePath());
		// serviceLibraryRepository.save(library);
		// file = new File(classLoader.getResource("probe/simple2secure.probe-0.1.2.jar").getFile());
		// library = new ServiceLibrary("Probe", "0.1.2", file.getAbsolutePath());
		// serviceLibraryRepository.save(library);

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Simple2SecurePortal.class);
	}

	@PostConstruct
	public void currentActiveProfile() {
		String[] activeProfiles = env.getActiveProfiles();

		for (String profile : activeProfiles) {
			if (profile.equals(StaticConfigItems.PROFILE_PRODUCTION)) {
				LoadedConfigItems loadedConfigItems = LoadedConfigItems.getInstance();
				loadedConfigItems.setBasePort("8443");
				loadedConfigItems.setBaseProtocol("https");
				loadedConfigItems.setBaseHost("localhost");
				loadedConfigItems.setBasePortWeb("9000");
			}
		}

	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(Simple2SecurePortal.class, args);
	}

}
