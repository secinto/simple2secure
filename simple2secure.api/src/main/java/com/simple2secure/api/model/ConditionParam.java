package com.simple2secure.api.model;

public class ConditionParam<T> {	
	private String name;
	private String description_en;
	private String description_de;
	private T value;
	private DataType type;
	
	public ConditionParam() {
		super();
	}	

	public ConditionParam(String name, String description_en, String description_de, T value, DataType type) {
		super();
		this.name = name;
		this.description_en = description_en;
		this.description_de = description_de;
		this.value = value;
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
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
	}
}
