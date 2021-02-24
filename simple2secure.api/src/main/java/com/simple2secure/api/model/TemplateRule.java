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

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.simple2secure.api.dbo.GenericDBObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 *		Class holds the a rule with the information of a predefined condition and action.
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRule extends GenericDBObject 
{

	private static final long serialVersionUID = -5156938336427387331L;

	/**
	 * Name of the rule.
	 */
	private String name;

	/**
	 * Description of the rule.
	 */
	private String description;

	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId contextID;

	/**
	 * This value will be used as threshold how often the conditions of this rule must be
	 * evaluated true at multiple independent inputs, before the action will be performed at
	 * last time.  
	 */
	private int limit;

	
	/**
	 * The logical expression how the different conditions must be evaluated so that the
	 * actions will be perfomed.
	 * 
	 * e.g.: (A & B) | C means that the first two conditions must be evaluated true or the
	 * third one, that the actions will be performed.
	 */
	private String conditionExpression;

	/**
	 * List of all chosen predefined conditions.
	 */
	private List<TemplateCondition> templateConditions;

	/**
	 * List of all chosen predefined actions.
	 */
	private List<TemplateAction> templateActions;

	public TemplateRule(String name, String description, ObjectId contextID,
			List<TemplateCondition> templateCondition,
			List<TemplateAction> templateAction) 
	{
		super();
		this.name = name;
		this.description = description;
		this.contextID = contextID;
		templateConditions = templateCondition;
		templateActions = templateAction;
	}
}
