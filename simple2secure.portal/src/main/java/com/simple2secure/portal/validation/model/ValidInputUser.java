package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputUser extends ValidatedInput<String> {

	public static String IS_AUTHENTICATED_TAG = "isAuthenticated";
	public static String USER_OBJECT_TAG = "user";

	private String userId;
	private String tag = "/{userId}";

	public ValidInputUser() {
	}

	public ValidInputUser(String userId) {
		this.userId = userId;
	}

	@Override
	public String getValue() {
		return userId;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Object validate(HttpServletRequest request, Map<String, Object> params) {
		if (((Boolean) params.get(IS_AUTHENTICATED_TAG))) {
			return new ValidInputUser((String) params.get(USER_OBJECT_TAG));
		} else {
			return null;
		}
	}

	@Override
	public Object validatePathVariable(String value) {
		// TODO Auto-generated method stub
		return null;
	}
}
