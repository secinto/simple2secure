package com.simple2secure.api.model;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

	private List<?> object = new ArrayList<>();
	private String clazz;

	public SearchResult() {
		// TODO Auto-generated constructor stub
	}

	public SearchResult(List<?> object, String clazz) {
		this.object = object;
		this.clazz = clazz;
	}

	public List<?> getObject() {
		return object;
	}

	public void setObject(List<?> object) {
		this.object = object;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

}
