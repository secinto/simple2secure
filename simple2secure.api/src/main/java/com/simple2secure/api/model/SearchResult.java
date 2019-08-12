package com.simple2secure.api.model;

public class SearchResult {

	private String result;
	private String clazz;

	public SearchResult() {
		// TODO Auto-generated constructor stub
	}

	public SearchResult(String result, String clazz) {
		this.result = result;
		this.clazz = clazz;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

}
