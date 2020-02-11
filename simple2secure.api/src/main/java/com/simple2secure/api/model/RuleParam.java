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
 * @author Richard Heinz
 * 
 *         Model which holds the information for one rule parameter. Will be used in the rule engine for the condition/action-parameters
 *
 * @param <T>
 *          generic for the type of the param value.
 */
@Getter
@Setter
public class RuleParam<T> {
	private String name_tag;
	private String description_tag;
	private T value;
	private DataType type;

	public RuleParam() {
		super();
	}

	public RuleParam(String name_tag, String description_tag, T value, DataType type) {
		super();
		this.name_tag = name_tag;
		this.description_tag = description_tag;
		this.value = value;
		this.type = type;
	}
}
