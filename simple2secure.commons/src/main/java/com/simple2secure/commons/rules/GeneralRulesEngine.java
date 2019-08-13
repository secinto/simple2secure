package com.simple2secure.commons.rules;

import java.io.IOException;
import java.util.List;

/**
 * @author Richard Heinz
 *
 */

public interface GeneralRulesEngine 
{
	public void addRuleFromSource(String source) 
			throws IOException, InstantiationException, IllegalAccessException;
	
	public void addRule(Object rule);

	
	public void removeRule(String ruleName);
	
	public void addFact(Object fact);
	
	public void removeFact(String classname);
	
	public void checkFacts();
}
