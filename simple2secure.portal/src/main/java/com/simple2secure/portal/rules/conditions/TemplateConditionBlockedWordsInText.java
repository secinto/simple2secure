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

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;

@AnnotationCondition(
		name = "find words in text",
		description_de = "Regel wird ausgel&oumlst wenn definierte W&oumlrter im Text gefunden werden",
		description_en = "Rule will be triggerd if defined words has been found in the text")
public class TemplateConditionBlockedWordsInText extends AbtractEmailCondition {

	@AnnotationRuleParamArray(
			name = "words to find",
			description_de = "Woerter die im Text gefunden werden m&uumlssen zum Ausl&oumlsen der Regel",
			description_en = "Words which must be found in the text to trigger the rule",
			type = DataType._STRING)
	private Collection<String> words;

	@Override
	protected boolean condition(Email email) {
		for (String word : words) {
			if (email.getText().toLowerCase().contains(word.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
