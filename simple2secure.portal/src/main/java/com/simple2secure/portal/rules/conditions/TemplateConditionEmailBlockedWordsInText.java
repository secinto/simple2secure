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

package com.simple2secure.portal.rules.conditions;

import java.util.Collection;

import org.bson.types.ObjectId;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;

@AnnotationCondition(
		name_tag = "email_rules_condition_name_find_words_in_text",
		description_tag = "email_rules_condition_description_find_words_in_text",
		fact_type = RuleFactType.EMAIL)
public class TemplateConditionEmailBlockedWordsInText extends AbstractPortalCondition<Email>
{

	@AnnotationRuleParamArray(
			name_tag = "email_rules_condition_paramarray_name_words_to_find_in_text",
			description_tag = "email_rules_condition_paramarray_description_words_to_find_in_text",
			type = DataType._STRING)
	private Collection<String> words;

	@Override
	protected boolean condition(Email email, ObjectId contextId) {
		for (String word : words) {
			if (email.getText().toLowerCase().contains(word.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
