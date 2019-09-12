package com.simple2secure.portal.rules.conditions;

import java.util.Collection;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;

@AnnotationCondition(name = "find words in subject",
description_de = "Regel wird ausgelöst wenn definierte Wörter im Betreff gefunden werden",
description_en = "Rule will be triggerd if defined words has been found in the subject")
public class TemplateConditionBlockedWordsInSubject extends AbtractEmailCondition {

	@AnnotationRuleParamArray(name = "words to find",
			description_de = "Woerter die im Betreff gefunden werden müssen zum Auslösen der Regel",
			description_en = "Words which must be found in the subject to trigger the rule",
			type = DataType._STRING)
	private Collection<String> words;

	
	@Override
	protected boolean condition(Email email) {
		for (String word : words) {
			if (email.getSubject().toLowerCase().contains(word.toLowerCase()))
				return true;
		}
		return false;
	}
}
