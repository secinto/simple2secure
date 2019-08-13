package com.simple2secure.test.portal.rules;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.simple2secure.portal.utils.NotificationUtils;

import com.simple2secure.api.model.Email;


@Rule(name = "find word",description = 
             "rule which finds specific words in an email message", priority = 
             2)
public class TestRule
{
	@Autowired
	NotificationUtils notificationUtils;
	
	@Condition
	public boolean serachingWordInText(@Fact("com.simple2secure.api.model.Email") Email email)
	{
		System.out.println(email.getText());
		if (email.getText().contains("spam"))
		{
			System.out.println("===================");
			return true;
		}
		return false;
	}
	
    
	@Action
	public void found(@Fact("com.simple2secure.api.model.Email") Email email) throws Exception
	{
		notificationUtils.addNewNotificationPortal("Test: found word", email.getConfigId());
	}
}