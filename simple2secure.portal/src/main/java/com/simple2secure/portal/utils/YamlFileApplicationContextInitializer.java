package com.simple2secure.portal.utils;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

public class YamlFileApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static Logger log = LoggerFactory.getLogger(YamlFileApplicationContextInitializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		try {
			Resource resource = applicationContext.getResource("classpath:application.yml");
			YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
			List<PropertySource<?>> yamlTestProperties = sourceLoader.load("yamlTestProperties", resource);
			if (yamlTestProperties != null && yamlTestProperties.size() > 0)
				applicationContext.getEnvironment().getPropertySources().addFirst(yamlTestProperties.get(0));
			else {
				log.error("Couldn't find YAML configuration file.");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}