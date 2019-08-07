package com.simple2secure.test.portal.rules;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.joda.time.DateTime;



@Rule(name = "check birthday",description = "checks if a person has birthday and sends a card", priority = 1)
public class RuleCheckBirthday 
{
	@Condition
	public boolean checkingDate(@Fact("com.simple2secure.test.portal.rules.Person") Person person)
	{
		DateTime today = new DateTime();
		if(today.getDayOfMonth() == person.getBirthDate().getDayOfMonth() &&
		   today.getMonthOfYear() == person.getBirthDate().getMonthOfYear())
			return true;
		return false;
	}
	
	@Action
	public void personHasBirthday(@Fact("com.simple2secure.test.portal.rules.Person") Person person)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("\n====================\n")
	           .append("== Happy BIRTHDAY ==\n")
	           .append("====================\n")
	           .append("\n")
	           .append("to your " )
	           .append(new DateTime().getYear() - person.getBirthDate().getYear())
	           .append(" Birthday!\n");

		PostOffice.sendMessage(person.getName(), person.getAddress(),
				builder.toString());
	}
}
