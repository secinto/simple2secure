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

package com.simple2secure.portal.rules;

import groovy.lang.GroovyClassLoader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import com.simple2secure.commons.rules.engine.GeneralRulesEngineImpl;


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
