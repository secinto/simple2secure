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
public class TemplateAction extends GenericDBObject {

	private static final long serialVersionUID = 3641649125428321240L;

	private String name;
	private String description_en;
	private String description_de;
	private List<RuleParam<?>> params;
	private List<RuleParamArray<?>> paramArrays;

	public TemplateAction(String name, String description_en, String description_de, List<RuleParam<?>> params,
			List<RuleParamArray<?>> paramArrays) {
		super();
		this.name = name;
		this.description_en = description_en;
		this.description_de = description_de;
		this.params = params;
		this.paramArrays = paramArrays;
	}

	public TemplateAction() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription_en() {
		return description_en;
	}

	public void setDescription_en(String description_en) {
		this.description_en = description_en;
	}

	public String getDescription_de() {
		return description_de;
	}

	public void setDescription_de(String description_de) {
		this.description_de = description_de;
	}

	public List<RuleParam<?>> getParams() {
		return params;
	}

	public void setParams(List<RuleParam<?>> params) {
		this.params = params;
	}

	public List<RuleParamArray<?>> getParamArrays() {
		return paramArrays;
	}

	public void setParamArray(List<RuleParamArray<?>> paramArray) {
		this.paramArrays = paramArray;
	}
}
