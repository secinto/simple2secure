package com.simple2secure.api.model.validation;

public abstract class ValidatedInput<T> {
	
	public abstract T getValue();
	
	public abstract String getTag();
}
