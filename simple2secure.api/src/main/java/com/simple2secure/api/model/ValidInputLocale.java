package com.simple2secure.api.model;

public class ValidInputLocale extends ValidatedInput{
	
	private String locale;
	
	public ValidInputLocale(String locale) {
		this.locale = locale;
	}

	@Override
	public String getValue() {
		return locale;
	}

	@Override
	public void setValue(String locale) {
		this.locale = locale;
	}
	
}
