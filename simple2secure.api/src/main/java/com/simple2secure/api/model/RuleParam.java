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

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * This class is used for every condition/action which has user defined parameter.
 * It holds the metadata and the value of a single parameter inside the condition/action class. 
 *
 * @param <T> generic type for parameter value.
 */
@Getter
@Setter
public class RuleParam<T> 
{
	
	/**
	 * Tag of the locale string for displaying the name of the parameter in the
	 * web user interface.
	 */
	private String nameTag;
	
	/**
	 * Tag of the locale string for displaying the description of the parameter in the
	 * web user interface.
	 */
	private String descriptionTag;
	
	/** 
	 * Actual value of the parameter.
	 */
	private T value;
	
	/**
	 * Enum to save the data type as metadata for displaying the right input field
	 * in the web user interface. 
	 */
	private DataType type;	


	public RuleParam() 
	{
		super();
	}

	public RuleParam(String nameTag, String descriptionTag, T value, DataType type) 
	{
		super();
		this.nameTag = nameTag;
		this.descriptionTag = descriptionTag;
		this.value = value;
		this.type = type;
	}
	
	/**
	 * Method to make a copy of an existing RuleParam object and setting a new value.
	 * 
	 * @param <T> datatype of the value.
	 * @param ruleParam which should be copied.
	 * @param value which should be set in the copy.
	 * @return the new created object.
	 */
	public static <T> RuleParam<T> copyAndSetValue(RuleParam<?> ruleParam, T value) 
	{
		RuleParam<T> copy = new RuleParam<T>(
				ruleParam.getNameTag(),
				ruleParam.getDescriptionTag(),
				value,
				ruleParam.getType());
		
		return copy;
	}
}
