package com.simple2secure.test.portal.rules;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.reflect.ClassPath;
import com.google.gson.Gson;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.EmailAttribute;
import com.simple2secure.api.model.EmailAttributeEnum;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.commons.rules.annotations.ConditionParam;
import com.simple2secure.commons.rules.annotations.ConditionParamArray;
import com.simple2secure.commons.rules.engine.GeneralRulesEngine;
import com.simple2secure.commons.rules.engine.GeneralRulesEngineImpl;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.dao.MongoRepository;
import com.simple2secure.portal.repository.EmailAttributesListRepository;
import com.simple2secure.portal.rules.EmailRulesEngine;
import com.simple2secure.portal.rules.PortalRulesEngine;
import com.simple2secure.portal.rules.TemplateActionSendNotification;
import com.simple2secure.portal.rules.TemplateEmailCondition;
import com.simple2secure.portal.rules.TemplateRuleBuilder;
import com.simple2secure.portal.rules.TemplateConditionFindWords;
import com.simple2secure.portal.rules.TemplateConditionSpecificDomains;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

//@ExtendWith({ SpringExtension.class })
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class })
//@ActiveProfiles("test")
public class TestRuleEngine {
	
	@Autowired
	private TemplateRuleBuilder templateRuleBuilder;
	
	/*
	@Autowired
	EmailAttributesListRepository emailAttributesListRepository;
	
	@Autowired
	PortalRulesEngine portalRulesEngine;
	
	@Autowired EmailRulesEngine emailRulesEngine;
*/
	
	/*
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
	
	

	private Email[] readJsonMails()
	{		
		String emails_directory = "C:\\Users\\Richard Heinz\\Desktop\\test_emails.json";
		
		try ( final FileReader fileReader = new FileReader(emails_directory) ){
			Email[] emails = new Gson().fromJson(fileReader, Email[].class);
	        
			return emails;
	    }
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	@Test void testEmailRulesEngine()
	{
		Email[] emails = readJsonMails();
		
		for(Email email : emails)
		{
			emailRulesEngine.addFact(email);
		}
	}
	
	/*
	@Test
	public void testPortalRuleEngine()
	{	
		
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
	
	*/
	
	public org.jeasy.rules.api.Rule testRuleTemplate() // only for testing
	{
		TemplateRule templateRule = new TemplateRule();	
		templateRule.setName("testRule");
		templateRule.setDescription("rule to test");
		templateRule.getContextID(); // todo
		templateRule.setConditionTemplate("find_words");
		String[] needles = {"test", "spam"};
		String[] domains = {"secinto.at", "gmail.com"};
		templateRule.setConditionParams(needles);
		templateRule.setActionTemplate("send_notification");
		String[] params = {"test worked fine!"};
		//String[] params = {"test worked fine!",  "richard.heinz@secinto.at"};
		templateRule.setActionParams(params);
		return templateRuleBuilder.build(templateRule);
	}
	
	
	@Test
	public void testConditionWithAnnotations()
	{
		String[] words = {"abc", "def"};
		TemplateConditionFindWords con = new TemplateConditionFindWords(words);

		Class clazz = con.getClass();
		
		Field fs[] = clazz.getDeclaredFields();
		for (Field f : fs) 
	    {
			f.setAccessible(true);
	    	System.out.println(f);

		    
		    if(f.getAnnotation(ConditionParamArray.class) instanceof ConditionParamArray)
		    {
		    	ConditionParamArray paramArray = (ConditionParamArray) f.getAnnotation(ConditionParamArray.class);
		    	
		    	System.out.println(paramArray.description_de());
		    	
		    	try {
		    		String[] array = {"new",  "words", "in", "array"};
					f.set(con, array);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    
		    if(f.getAnnotation(ConditionParam.class) instanceof ConditionParam)
		    {
		    	ConditionParam param = (ConditionParam) f.getAnnotation(ConditionParam.class);
		    	
		    	System.out.println(param.description_de());
		    	System.out.println(param.name());
		    	
		    	try {
			    	if(param.name().contentEquals("limited word"))
			    		f.set(con, "new_word");

			    	
			    	if(param.name().contentEquals("max word count"))
			    		f.set(con, 100);
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
	    }
	}
	
	@Test
	public void testLoadingClassesFromPackage()
	{
		TemplateActionSendNotification temp = new TemplateActionSendNotification("asdf");
		ClassLoader loader = temp.getClass().getClassLoader();
	    ClassPath path;
		try {
			path = ClassPath.from(loader);
			for (ClassPath.ClassInfo info : path.getTopLevelClassesRecursive("com.simple2secure.test.portal.rules")) {
		        try {
					Class clazz = Class.forName(info.getName(), true, loader);
					System.out.println(clazz.getName());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        //dowhatever
		    }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}


