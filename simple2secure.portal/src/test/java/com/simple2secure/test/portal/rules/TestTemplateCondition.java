package com.simple2secure.test.portal.rules;

import java.util.Collection;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;
import com.simple2secure.portal.rules.conditions.AbtractEmailCondition;

/**
 * 
 * @author Richard Heinz
 * 
 * Action class for testing. DO NOT change otherwise the test cases will not 
 * perform right.
 */
/**
 * @author Richard Heinz
 *
 */
@AnnotationCondition(name = "test condition", description_de = "action description_de", description_en = "action description_en")
public class TestTemplateCondition extends AbtractEmailCondition {

	@AnnotationRuleParam(name = "varInt", description_de = "varInt description_de", description_en = "varInt description_en", type = DataType._INT)
	int varInt;

	@AnnotationRuleParam(name = "varDouble", description_de = "varDouble description_de", description_en = "varDouble description_en", type = DataType._DOUBLE)
	double varDouble;

	@AnnotationRuleParam(name = "varString", description_de = "varString description_de", description_en = "varString description_en", type = DataType._STRING)
	String varString;

	@AnnotationRuleParamArray(name = "arrayInt", description_de = "arrayInt description_de", description_en = "arrayInt description_en", type = DataType._INT)
	Collection<Integer> arrayInt;

	@AnnotationRuleParamArray(name = "arrayDouble", description_de = "arrayDouble description_de", description_en = "arrayDouble description_en", type = DataType._DOUBLE)
	Collection<Double> arrayDouble;

	@AnnotationRuleParamArray(name = "arrayString", description_de = "arrayString description_de", description_en = "arrayString description_en", type = DataType._STRING)
	Collection<String> arrayString;

	@Override
	protected boolean condition(Email email) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getVarInt() {
		return varInt;
	}

	public double getVarDouble() {
		return varDouble;
	}

	public String getVarString() {
		return varString;
	}

	public Collection<Integer> getArrayInt() {
		return arrayInt;
	}

	public Collection<Double> getArrayDouble() {
		return arrayDouble;
	}

	public Collection<String> getArrayString() {
		return arrayString;
	}

}
