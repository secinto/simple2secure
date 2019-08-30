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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RuleParamArray<T> {
	private String name;
	private String description_en;
	private String description_de;
	private Collection<T> values;
	private DataType type;
	
	public RuleParamArray() {
		super();
	}
	
	public RuleParamArray(String name, String description_en, String description_de, Collection<T> values,
			DataType type) {
		super();
		this.name = name;
		this.description_en = description_en;
		this.description_de = description_de;
		this.values = values;
		this.type = type;
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
	public Collection<T> getValues() {
		return values;
	}
	public void setValues(List<T> paramArray) {
		this.values = paramArray;
	}
	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
	}
}
