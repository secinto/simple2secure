package com.simple2secure.portal.rules.conditions;

import java.util.Collection;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;

@AnnotationCondition(name = "find words",
description_de = "Regel wird ausgelöst wenn definierte Wörter gefunden werden",
description_en = "Rule will be triggerd if defined words has been found")
public class TemplateConditionFindWords extends AbtractEmailCondition {

	@AnnotationRuleParamArray(name = "words to find",
			description_de = "Woerter die gefunden werden müssen zum Auslösen der Regel",
			description_en = "Words which must be found to trigger the rule",
			type = DataType._String)
	private Collection<String> words;
	
	@AnnotationRuleParam(name = "limited word",
			description_de = "Weiteres wort welches nur maximal x mal vorkommen darf",
			description_en = "one more word which is allowed max x times",
			type = DataType._String)
	private String word;
	
	@AnnotationRuleParam(name = "max word count",
			description_de = "Anzahl wie oft das vorgegeben Wort vorkommen darf",
			description_en = "Number how often the given word is allowed",
			type = DataType._int)
	private int max_word_count;
	
	/*
	public TemplateConditionFindWords(String[] words) {
		this.words = words;
		word = "abc";
		max_word_count = 1;
	}
	*/
	

	@Override
	protected boolean condition(Email email) {
		for (String word : words) {
			if (email.getText().contains(word))
				return true;
		}
		return false;
	}
}
