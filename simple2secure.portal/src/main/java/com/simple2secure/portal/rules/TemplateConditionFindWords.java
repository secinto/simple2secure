package com.simple2secure.portal.rules;

import com.simple2secure.api.model.Email;
import com.simple2secure.commons.rules.annotations.ConditionParam;
import com.simple2secure.commons.rules.annotations.ConditionParamArray;
import com.simple2secure.commons.rules.enums.DataType;

public class TemplateConditionFindWords extends TemplateEmailCondition {

	//@ConditionParamArray("words", "Words which need to be contained in the email content in order that the condition is triggered.")
	@ConditionParamArray(name = "words to find",
			description_de = "Woerter die gefunden werden müssen zum Auslösen der Regel",
			description_en = "Words which must be found to trigger the rule",
			type = DataType._String)
	private String[] words;
	
	@ConditionParam(name = "limited word",
			description_de = "Weiteres wort welches nur maximal x mal vorkommen darf",
			description_en = "one more word which is allowed max x times",
			type = DataType._String)
	private String word;
	
	@ConditionParam(name = "max word count",
			description_de = "Anzahl wie oft das vorgegeben Wort vorkommen darf",
			description_en = "Number how often the given word is allowed",
			type = DataType._int)
	private int max_word_count;
	
	
	public TemplateConditionFindWords(String[] words) {
		this.words = words;
		word = "abc";
		max_word_count = 1;
	}

	@Override
	protected boolean condition(Email email) {
		for (String word : words) {
			if (email.getText().contains(word))
				return true;
		}
		return false;
	}
}
