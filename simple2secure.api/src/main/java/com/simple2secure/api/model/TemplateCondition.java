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

package com.simple2secure.api.model;

import java.util.List;

import com.simple2secure.api.dbo.GenericDBObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 *      Class holds the metadata and the values of each parameter of a predefined condition
 *      for the rule engine for saving it in the the database.
 *      The user defined values of the parameter will be loaded from this class into 
 *      the actual condition class at runtime.
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateCondition extends GenericDBObject
{

	private static final long serialVersionUID = -1291327703141018318L;

	/**
	 * Name of the class which belongs to the metadata and parameter values of this class.
	 */
	private String className;
	
	/**
	 * Tag of the locale string for displaying the name of the condition in the
	 * web user interface. 
	 */
	private String nameTag;
	
	/**
	 * Tag of the locale string for displaying the description of the condition in the
	 * web user interface.
	 */
	private String descriptionTag;
	
	/**
	 * Type of the fact the action has been designed.
	 */
	private RuleFactType factType;
	
	/**
	 * List with all RuleParam objects from the actual action class
	 */
	private List<RuleParam<?>> params;
	
	/**
	 * List of all RuleParamArray objects form the actual action class.
	 */
	private List<RuleParamArray<?>> paramArrays;

}
