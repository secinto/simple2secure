package com.simple2secure.api.model;

import java.util.Collection;
import java.util.List;

public class TemplateCondition {
	private String name;
	private String description_en;
	private String description_de;
	private Collection<ConditionParam<?>> params;
	private Collection<ConditionParamArray<?>> paramArrays;
	
	public TemplateCondition(String name, String description_en, String description_de, Collection<ConditionParam<?>> params,
			Collection<ConditionParamArray<?>> paramArrays) {
		super();
		this.name = name;
		this.description_en = description_en;
		this.description_de = description_de;
		this.params = params;
		this.paramArrays = paramArrays;
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

	public Collection<ConditionParam<?>> getParams() {
		return params;
	}

	public void setParams(Collection<ConditionParam<?>> params) {
		this.params = params;
	}

	public Collection<ConditionParamArray<?>> getParamArray() {
		return paramArrays;
	}

	public void setParamArray(Collection<ConditionParamArray<?>> paramArray) {
		this.paramArrays = paramArray;
	}
}
