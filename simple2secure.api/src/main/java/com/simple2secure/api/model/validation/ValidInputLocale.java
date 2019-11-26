package com.simple2secure.api.model.validation;

public class ValidInputLocale extends ValidatedInput<String>{
	
	private String locale;
	
	public ValidInputLocale() {
		
	}
	
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

	@Override
	public String getTag() {
		// TODO Auto-generated method stub
		return null;
	}	
}
