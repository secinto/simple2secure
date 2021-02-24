/**
*********************************************************************
*   simple2secure is a cyber risk and information security platform.
*   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
*********************************************************************
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU Affero General Public License as
*   published by the Free Software Foundation, either version 3 of the
*   License, or (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*   GNU Affero General Public License for more details.
*
*   You should have received a copy of the GNU Affero General Public License
*   along with this program.  If not, see <https://www.gnu.org/licenses/>.
*  
 *********************************************************************
*/

package com.simple2secure.test.portal.rules;

import java.util.Collection;

import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;

import lombok.Getter;

/**
 * 
 * @author Richard Heinz
 * 
 * Action class for testing. <strong>DO NOT change otherwise the test cases will not perform right.</strong>
 */
/**
 * @author Richard Heinz
 *
 */
@AnnotationCondition(
	name_tag = "test_condition_tag", 
	description_tag = "action_description_tag",
	fact_type = RuleFactType.GENERAL)
@Getter
public class TestTemplateCondition implements Condition  {

	@AnnotationRuleParam(name_tag = "varInt_tag", description_tag = "varInt_description_tag", type = DataType._INT)
	int varInt;

	@AnnotationRuleParam(name_tag = "varDouble_tag", description_tag = "varDouble_description_tag", type = DataType._DOUBLE)
	double varDouble;

	@AnnotationRuleParam(name_tag = "varString_tag", description_tag = "varString_description_tag",  type = DataType._STRING)
	String varString;

	@AnnotationRuleParamArray(name_tag = "arrayInt_tag", description_tag = "arrayInt_description_tag", type = DataType._INT)
	Collection<Integer> arrayInt;

	@AnnotationRuleParamArray(name_tag = "arrayDouble_tag", description_tag = "arrayDouble_description_tag", type = DataType._DOUBLE)
	Collection<Double> arrayDouble;

	@AnnotationRuleParamArray(name_tag = "arrayString_tag", description_tag = "arrayString_description_tag", type = DataType._STRING)
	Collection<String> arrayString;

	@Override
	public boolean evaluate(Facts facts) {
		return false;
	}
}
