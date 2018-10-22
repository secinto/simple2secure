/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal;

import java.util.Locale;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.simple2secure.api.config.ConfigItems;
import com.simple2secure.portal.utils.DataInitialization;



@SpringBootApplication(scanBasePackages = { "com.simple2secure.portal" }, exclude = {EmbeddedMongoAutoConfiguration.class,
		MongoAutoConfiguration.class })
@EnableScheduling
public class Simple2SecurePortal extends SpringBootServletInitializer {

	@Bean
	WebMvcConfigurer configurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/config/**").addResourceLocations(ConfigItems.resource_location);
			}
		};
	}
	
    @Bean
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        return slr;
    }
    
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:locale/messages");
        messageSource.setCacheSeconds(3600); //refresh cache once per hour
        return messageSource;
    }    
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Simple2SecurePortal.class);
    }
    
	/**
	 * This function initiates http on port 8080 but redirects all trafic to https
	 * @return
	 */
	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
		    @Override
		    protected void postProcessContext(Context context) {
		      SecurityConstraint securityConstraint = new SecurityConstraint();
		      securityConstraint.setUserConstraint("CONFIDENTIAL");
		  SecurityCollection collection = new SecurityCollection();
		  collection.addPattern("/*");
		      securityConstraint.addCollection(collection);
		      context.addConstraint(securityConstraint);
		    }
		};
	    
	  	tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
		return tomcat;
	}
	  
	/**
	 * Initiate new http connection on port 8080
	 * @return
	 */
	private Connector initiateHttpConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		connector.setPort(8080);
		connector.setSecure(false);
		connector.setRedirectPort(8443);
		
		return connector;
	}    

	public static void main(String[] args) {
		SpringApplication.run(Simple2SecurePortal.class, args);
		DataInitialization.addDefaultSettings();
	}
	
}
