package com.simple2secure.commons.rules;

import java.io.IOException;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import groovy.lang.GroovyClassLoader;

/**
 * @author Richard Heinz
 *
 */
//@Service
public class GeneralRulesEngineImpl implements GeneralRulesEngine {

	private static Logger log = LoggerFactory.getLogger(GeneralRulesEngineImpl.class);
	

	private Facts facts_ = new Facts();
	protected Rules rules_ = new Rules();
	private RulesEngine rules_engine_ = new DefaultRulesEngine();

	
	/**
	 * Method to load sourcecode from string, creates rule and register it.
	 * 
	 * Attention: Does not support spring framework in source!
	 * 
	 * @param source which contains the sourcecode of a rule class
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public void addRuleFromSource(String source)
			throws IOException, InstantiationException, IllegalAccessException
	{
		try (GroovyClassLoader groovyClassLoader = new GroovyClassLoader()) {
			Class<?> theParsedClass = groovyClassLoader.parseClass(source);

			Object rule = theParsedClass.newInstance();
			//autowireCapableBeanFactory.autowireBean(rule);
			rules_.register(rule); // will automatic make a rule object in the background
			log.debug("Registered new rule {} with GroovyClassLoader", rule.getClass().getName());
		}
	}


	/**
	 * Method to register new rule
	 * 
	 * @param rule object
	 */
	@Override
	public void addRule(Object rule) {
		rules_.register(rule);
		log.debug("Registered new rule {}", rule.getClass().getName());
	}

	
	/**
	 * Method to delete rule by name
	 * 
	 * @param ruleName represents the name of the rule not class name!
	 */
	@Override
	public void removeRule(String ruleName) {
		rules_.unregister(ruleName);
		log.debug("Removed rule \"{}\"", ruleName);
	}
	

	/**
	 * Method to add fact 
	 * 
	 * @param fact any object. If the object is same type of class as a saved 
	 * fact, the old one will be overridden. (HashMap in the background).
	 */
	@Override
	public void addFact(Object fact) {
		String fact_classname = fact.getClass().getName();
		facts_.put(fact_classname, fact);		
		log.debug("Added fact {}", fact_classname);
	}

	
	/**
	 * Method to remove known fact by classname
	 * 
	 * @param classname is the full classname with packages as prefix
	 */
	@Override
	public void removeFact(String classname) {
		facts_.remove(classname);
		log.debug("Removed fact {}", classname);
	}

	
	/**
	 * Method to check all known facts with registered rules
	 */
	@Override
	public void checkFacts() {
		log.debug("Started rule engine");
		rules_engine_.fire(rules_, facts_);
	}
}












