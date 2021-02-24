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

package com.simple2secure.commons.rules.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.simple2secure.api.model.RuleFactType;


/**
 * 
 *		Annotation do mark a class as a action for the rule engine. The metadata will the be saved
 *      in the database and displayed the user in the web user interface when defining a new rule.
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationAction 
{
	/**
	 * Tag of the locale string for displaying the name of the action in the
	 * web user interface.
	 */
	String name_tag();

	/**
	 * Tag of the locale string for displaying the description of the action in the
	 * web user interface.
	 */
	String description_tag();
	
	/**
	 * Type of the fact the action has been designed.
	 */
	RuleFactType fact_type();
}
