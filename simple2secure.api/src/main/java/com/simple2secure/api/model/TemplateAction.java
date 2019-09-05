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

import java.util.Collection;

import com.simple2secure.api.dbo.GenericDBObject;

public class TemplateAction extends GenericDBObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3641649125428321240L;
	
	private String name;
	private String description_en;
	private String description_de;
	private Collection<RuleParam<?>> params;
	private Collection<RuleParamArray<?>> paramArrays;
	
	public TemplateAction(String name, String description_en, String description_de, Collection<RuleParam<?>> params,
			Collection<RuleParamArray<?>> paramArrays) {
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

	public Collection<RuleParam<?>> getParams() {
		return params;
	}

	public void setParams(Collection<RuleParam<?>> params) {
		this.params = params;
	}

	public Collection<RuleParamArray<?>> getParamArrays() {
		return paramArrays;
	}

	public void setParamArray(Collection<RuleParamArray<?>> paramArray) {
		this.paramArrays = paramArray;
	}
}
