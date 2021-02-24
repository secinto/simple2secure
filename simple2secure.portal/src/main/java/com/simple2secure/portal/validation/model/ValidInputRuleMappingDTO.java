package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import com.simple2secure.api.dto.RuleMappingDTO;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputRuleMappingDTO  extends ValidatedInput<String> {

	private RuleMappingDTO ruleMappingDTO;
	private String tag = "/{ruleMappingDTO}";
	
	@Override
	public RuleMappingDTO getValue() {
		return ruleMappingDTO;
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
		return new ValidInputRuleMappingDTO();
	}

}
