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

package com.simple2secure.commons.rules.engine;

import java.util.Set;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *         Implementation of a rule engine with different use cases.
 *         For the engine is the easy-rules library (https://github.com/j-easy/easy-rules) Version 3.3.0 used.
 *         
 *         You can add and remove rules. A rule must have a condition and an action.
 *         If the the condition is satisfied the action will be performed.
 * 
 *         You can add and remove Facts, each rule will be used on each fact.
 * 
 */
public class GeneralRulesEngineImpl implements GeneralRulesEngine {

	private static Logger log = LoggerFactory.getLogger(GeneralRulesEngineImpl.class);

	/**
	 * The input for every rule.
	 */
	private Facts facts = new Facts();
	
	/**
	 * Rules which will be used on the facts.
	 */
	private Rules rules = new Rules();
	
	/**
	 * The core for the rule engine. Uses every rule one after the other when checking the facts.
	 */
	private RulesEngine engine = new DefaultRulesEngine();

	/**
	 * Method to register a new rule which will be
	 * used when calling the function {@link #checkFacts()}.
	 * 
	 * @param rule	Rule 
	 *          
	 */
	@Override
	public void addRule(Rule rule)
	{
		rules.register(rule);
		log.info("Registered new rule: \"{}\"", rule.getName());
	}

	/**
	 * Method to delete rule by name.
	 * 
	 * @param ruleName 	String which represents the name of
	 * 					the rule which should be deleted.
	 */
	@Override
	public void removeRule(String ruleName) 
	{
		rules.unregister(ruleName);
		log.info("Removed rule: \"{}\"", ruleName);
	}

	/**
	 * Method to add a new fact.
	 * 
	 * @param fact Any object can be a fact.
	 *  The classname will be saved as key with the given object as value in a Map.
	 * <strong>If the object is same type of class as a saved fact,
	 *  the old one will be overridden. (HashMap in the background).</strong>
	 */
	@Override
	public void addFact(Object fact) 
	{
		String factClassname = fact.getClass().getName();
		facts.put(factClassname, fact);
		log.info("Added fact \"{}\"", factClassname);
	}

	/**
	 * Method to remove known fact by classname.
	 * 
	 * @param classname String which represents the classnama.
	 *          		<strong>the classname must be with packages as prefix<\strong>
	 */
	@Override
	public void removeFact(String classname)
	{
		facts.remove(classname);
		log.info("Removed fact \"{}\"", classname);
	}

	/**
	 * Method to check all known facts with all registered rules.
	 */
	@Override
	public void checkFacts()
	{
		log.info("Started rule engine with registered rules.");
		engine.fire(rules, facts);
	}

	/**
	 * Method to remove all registered rules.
	 */
	@Override
	public void removeAllRules() 
	{
		rules.clear();
	}

	/** 
	 * Method to add a set of rules.
	 */
	@Override
	public void addRules(Set<Rule> rules) 
	{
		rules.forEach(this::addRule);
	}
}
