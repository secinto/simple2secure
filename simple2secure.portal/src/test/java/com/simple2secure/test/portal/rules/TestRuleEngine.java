package com.simple2secure.test.portal.rules;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.google.gson.Gson;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.EmailAttribute;
import com.simple2secure.api.model.EmailAttributeEnum;
import com.simple2secure.commons.rules.GeneralRulesEngine;
import com.simple2secure.commons.rules.GeneralRulesEngineImpl;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.repository.EmailAttributesListRepository;
import com.simple2secure.portal.rules.PortalRulesEngine;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestRuleEngine {
	
	@Autowired
	EmailAttributesListRepository emailAttributesListRepository;
	
	@Autowired
	PortalRulesEngine portalRulesEngine;

	
	@Test
	public void testFillEmailAttributeRepository()
	{		
		try
		{
			// saving email addresses for the whitelist
			if(emailAttributesListRepository.findByAttributeType(
					EmailAttributeEnum.WHITELIST).isEmpty())
			{
				emailAttributesListRepository.save(new EmailAttribute(
						"alice@testsecinto.com", EmailAttributeEnum.WHITELIST));
				
				emailAttributesListRepository.save(new EmailAttribute(
						"bob@testsecinto.com", EmailAttributeEnum.WHITELIST));
			}
			
			// saving email addresses for the blacklist
			if(emailAttributesListRepository.findByAttributeType(
					EmailAttributeEnum.BLACKLIST).isEmpty())
			{
				emailAttributesListRepository.save(new EmailAttribute(
						"alice@hacker.com",  EmailAttributeEnum.BLACKLIST));
				
				emailAttributesListRepository.save(new EmailAttribute(
						"bob@hacker.com",  EmailAttributeEnum.BLACKLIST));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPortalRuleEngine()
	{
		Collection<Email> mails = new ArrayList<Email>();
		
		Email mail1 = new Email();
		mail1.setFrom("alice@hacker.com");
		mail1.setSubject("asdf");
		mail1.setText("some textDOWNLOAD");
		mails.add(mail1);
		
		Email mail2 = new Email();
		mail2.setFrom("normal1@normal.com");
		mail2.setSubject("asdf");
		mail2.setText("some text");
		mails.add(mail2);
		
		try {			
			String email_rule_as_string1 = new Gson().fromJson(new FileReader(
					"src/test/resources/rules/json_rule_forbidden_words.json"),
					RuleClassAsString.class).getSourcecode();
			
			String email_rule_as_string2 = new Gson().fromJson(new FileReader(
					"src/test/resources/rules/json_rule_blocked_addresses.json"),
					RuleClassAsString.class).getSourcecode();

			portalRulesEngine.addRuleFromSourceWithBean(email_rule_as_string1);
			portalRulesEngine.addRuleFromSourceWithBean(email_rule_as_string2);
			
			mails.forEach(mail -> {
				portalRulesEngine.addFact(mail);
			    portalRulesEngine.checkFacts();
//			    System.out.println("Is mail Spam: " + mail.isSpam());
//			    System.out.println(mail.getSpamInfoAsString());
			    }
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGeneralRuleEngine() 
	{
		Person person = new Person(
				new DateTime(1950, 8, 5, 0, 0), "Max", "Musterweg 42");
		
		
		try {
			GeneralRulesEngine rule_engine = new GeneralRulesEngineImpl();
			
			rule_engine.addRule(new RuleCheckBirthday());
			rule_engine.removeRule("check birthday");
			
			
			String birthday_rule_as_string = new Gson().fromJson(new FileReader(
					"src/test/resources/rules/rule_birthday.json"),
					RuleClassAsString.class).getSourcecode();
			
			rule_engine.addRuleFromSource(birthday_rule_as_string);

			rule_engine.addFact(person);
			
			rule_engine.checkFacts();

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
