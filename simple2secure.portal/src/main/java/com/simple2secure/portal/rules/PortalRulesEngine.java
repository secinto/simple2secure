package com.simple2secure.portal.rules;

import com.simple2secure.commons.rules.GeneralRulesEngineImpl;


import groovy.lang.GroovyClassLoader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

/**
 * @author Richard Heinz
 *
 */
@Service
public class PortalRulesEngine extends GeneralRulesEngineImpl{
	/*
	 * AutowireCapableBeanFactory is needed to make a new instance of the class
	 * which has been imported with groovy. Otherwise the Spring Framework in the
	 * imported class won't work
	 */
	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	
	private static Logger log = LoggerFactory.getLogger(PortalRulesEngine.class);
	
	
	/**
	 * Method to load sourcecode(which uses Spring) from string, creates rule and register it
	 * 
	 * @param source which contains the sourcecode of a rule class
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void addRuleFromSourceWithBean(String source) 
			throws IOException, InstantiationException, IllegalAccessException

	{
		try (GroovyClassLoader groovyClassLoader = new GroovyClassLoader()) {
			Class<?> theParsedClass = groovyClassLoader.parseClass(source);

			Object rule = theParsedClass.newInstance();
			autowireCapableBeanFactory.autowireBean(rule);
			rules_.register(rule); // will automatic make a rule object in the background
			log.debug("Registered new rule {} with GroovyClassLoader", rule.getClass().getName());
		}
	}
}
