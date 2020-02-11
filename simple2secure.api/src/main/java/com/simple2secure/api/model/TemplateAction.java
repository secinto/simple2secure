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

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Richard Heinz
 * 
 *         Class holds the information of a predefined action-class which has been annotated as action. The params will be loaded from this
 *         class for each predefined action.
 * 
 *         Will be used in the rule engine for the condition/action-parameters
 *
 */
@Getter
@Setter
public class TemplateAction extends GenericDBObject {

	private static final long serialVersionUID = 3641649125428321240L;

	private String name_tag;
	private String description_tag;
	private List<RuleParam<?>> params;
	private List<RuleParamArray<?>> paramArrays;

	public TemplateAction(String name, String description_tag, List<RuleParam<?>> params, List<RuleParamArray<?>> paramArrays) {
		super();
		this.name_tag = name;
		this.description_tag = description_tag;
		this.params = params;
		this.paramArrays = paramArrays;
	}

	public TemplateAction() {
	}
}
