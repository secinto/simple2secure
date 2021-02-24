package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputGroup extends ValidatedInput<String> {

	private ObjectId groupId;
	private String tag = "/{groupId}";

	public ValidInputGroup() {
	}

	public ValidInputGroup(String groupId) {
		this.groupId = new ObjectId(groupId);
	}

	@Override
	public ObjectId getValue() {
		return groupId;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Object validate(HttpServletRequest request, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object validatePathVariable(String value) {
		return new ValidInputGroup(value);
	}
}
