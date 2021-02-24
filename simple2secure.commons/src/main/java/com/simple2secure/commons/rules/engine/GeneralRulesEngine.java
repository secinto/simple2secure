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

import org.jeasy.rules.api.Rule;

/**
 * 
 * Interface for a general rule engine which uses the library
 * easy-rules (https://github.com/j-easy/easy-rules) Version 3.3.0.
 *
 */
public interface GeneralRulesEngine
{
	public void addRule(Rule rule);
	
	public void addRules(Set<Rule> rules);

	public void removeRule(String ruleName);
	
	public void removeAllRules();

	public void addFact(Object fact);

	public void removeFact(String className);
	
	public void checkFacts();
}
