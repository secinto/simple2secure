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



import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Richard Heinz
 * 
 * Implementation for a rule engine for different use cases.
 * 
 * For the engine is the easy-rules library (https://github.com/j-easy/easy-rules) 
 * Version 3.3.0 used.
 * 
 * You can add and remove rules. A rule must have a condition and an action. If 
 * the the condition is satisfied the action will be performed.
 * 
 *  You can add and remove Facts, each rule will be used on each fact.
 * 
 *
 */
public class GeneralRulesEngineImpl implements GeneralRulesEngine {

	private static Logger log = LoggerFactory.getLogger(GeneralRulesEngineImpl.class);
	
	private Facts facts_ = new Facts();
	protected Rules rules_ = new Rules();
	private RulesEngine rules_engine_ = new DefaultRulesEngine();


	/**
	 * Method to register a new rule
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












