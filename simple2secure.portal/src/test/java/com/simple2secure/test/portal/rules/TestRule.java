package com.simple2secure.test.portal.rules;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import com.simple2secure.api.model.Email;

@Rule(name = "Test Rule", description = "A Rule for testing", priority = 2)
public class TestRule {
	@Condition
	public boolean check(@Fact("com.simple2secure.api.model.Email") Email email) {
		return true;
	}

	@Action
	public void execute(@Fact("com.simple2secure.api.model.Email") Email email) {

	}
}