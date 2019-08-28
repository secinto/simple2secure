package com.simple2secure.api.model;

import java.util.List;

public class ConditionParamArray<T> {
	private String name;
	private String description_en;
	private String description_de;
	private List<T> values;
	private DataType type;
	
	public ConditionParamArray() {
		super();
	}
	
	public ConditionParamArray(String name, String description_en, String description_de, List<T> values,
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
	public List<T> getValues() {
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
