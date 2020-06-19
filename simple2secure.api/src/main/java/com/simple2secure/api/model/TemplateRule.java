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

import com.simple2secure.api.dbo.GenericDBObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Richard Heinz
 * 
 *         Class holds the a rule with the information af a predefined conditionand action.
 * 
 *         Will be used in the rule engine.
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class TemplateRule extends GenericDBObject {

	private static final long serialVersionUID = -5156938336427387331L;

	private String name;

	private String description;

	private String contextID;

	private TemplateCondition templateCondition;

	private TemplateAction templateAction;

	public TemplateRule(String name, String description, String contextID, TemplateCondition templateCondition,
			TemplateAction templateAction) {
		super();
		this.name = name;
		this.description = description;
		this.contextID = contextID;
		this.templateCondition = templateCondition;
		this.templateAction = templateAction;
	}
}
