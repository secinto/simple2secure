package com.simple2secure.test.portal.rules;


import java.awt.List;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
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
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.api.model.ConditionParam;
import com.simple2secure.api.model.ConditionParamArray;
import com.simple2secure.api.model.DataType;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationConditionParam;
import com.simple2secure.commons.rules.annotations.AnnotationConditionParamArray;
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
	    

		    
		    if(f.getAnnotation(AnnotationConditionParamArray.class) instanceof AnnotationConditionParamArray)
		    {
		    	AnnotationConditionParamArray paramArray = (AnnotationConditionParamArray) f.getAnnotation(AnnotationConditionParamArray.class);
		    	
		    	System.out.println(paramArray.description_de());
		    	
		    	try {
		    		String[] array = {"new",  "words", "in", "array"};
					f.set(con, array);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    
		    if(f.getAnnotation(AnnotationConditionParam.class) instanceof AnnotationConditionParam)
		    {
		    	AnnotationConditionParam param = (AnnotationConditionParam) f.getAnnotation(AnnotationConditionParam.class);
		    	
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
		System.out.println("======================================================");
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    ClassPath path;
	    
	    //JSONArray conditionsInfoArray = new JSONArray();
	    
		try {
			
			Collection<TemplateCondition> conditions = new ArrayList<TemplateCondition>();
			
			path = ClassPath.from(loader);
			for (ClassPath.ClassInfo info : path.getTopLevelClassesRecursive("com.simple2secure.portal.rules")) {
				Class clazz = Class.forName(info.getName(), true, loader);
				AnnotationCondition annotationCondition = (AnnotationCondition) clazz.getAnnotation(AnnotationCondition.class);
				//JSONObject conditionsInfo = new JSONObject();

				 if(annotationCondition != null)
				    {	
					    Collection<ConditionParam<?>> conditionParams = new ArrayList<ConditionParam<?>>();
					    Collection<ConditionParamArray<?>> conditionParamArrays = new ArrayList<ConditionParamArray<?>>();
					    
				    	Field fs[] = clazz.getDeclaredFields();

						for (Field f : fs) 
					    {
							f.setAccessible(true);

							AnnotationConditionParamArray annotationParamArray = (AnnotationConditionParamArray) f.getAnnotation(AnnotationConditionParamArray.class);
						    if(annotationParamArray != null)
						    {			
						    	ConditionParamArray<?> paramArray = null;
						    	switch(annotationParamArray.type())
						    	{
						    	case _int:
						    		paramArray = new ConditionParamArray<Integer>();
						    		paramArray.setType(DataType._int);
						    		break;
						    		
						    	case _double:
						    		paramArray = new ConditionParamArray<Double>();
						    		paramArray.setType(DataType._double);
						    		break;
						    		
						    	case _String:
						    		paramArray = new ConditionParamArray<Double>();
						    		paramArray.setType(DataType._String);
						    		break;
						    		
						    	default:
						    		break;
						    	}
						    	
						    	if(paramArray != null)
						    	{
						    		paramArray.setName(annotationParamArray.name());
							    	paramArray.setDescription_de(annotationParamArray.description_de());
							    	paramArray.setDescription_en(annotationParamArray.description_en());
							    	paramArray.setValues(null);
							    	conditionParamArrays.add(paramArray);
						    	}
						    	
						    	continue;
						    }
						    
						    AnnotationConditionParam annotationParam = (AnnotationConditionParam) f.getAnnotation(AnnotationConditionParam.class);
						    if(annotationParam != null)
						    {
								
						    	ConditionParam<?> param = null;
						    	switch(annotationParam.type())
						    	{
						    	case _int:
						    		param = new ConditionParam<Integer>();
						    		param.setType(DataType._int);
						    		break;
						    		
						    	case _double:
						    		param = new ConditionParam<Double>();
						    		param.setType(DataType._double);
						    		break;
						    		
						    	case _String:
						    		param = new ConditionParam<Double>();
						    		param.setType(DataType._String);
						    		break;
						    		
						    	default:
						    		break;
						    	}
						    	
						    	if(param != null)
						    	{
						    		param.setName(annotationParam.name());
							    	param.setDescription_de(annotationParam.description_de());
							    	param.setDescription_en(annotationParam.description_en());
							    	param.setValue(null);
							    	conditionParams.add(param);	
						    	}
						    	
						    	continue;
						   }
						    
					    }
						
						TemplateCondition conditionObj = new TemplateCondition(
					    		annotationCondition.name(), 
					    		annotationCondition.description_en(), 
					    		annotationCondition.description_de(),
					    		conditionParams, conditionParamArrays);
						
						conditions.add(conditionObj);
						
				    }
				
				/*
			    if(condition != null)
			    {
			    	conditionsInfo.put("condition name", condition.name());
			    	conditionsInfo.put("condition_description_de", condition.description_de());
			    	conditionsInfo.put("condition_description_en", condition.description_en());
					System.out.println(clazz.getName());
			    	System.out.println(condition.name());
			    	
			    	Field fs[] = clazz.getDeclaredFields();
			    	int paramArrayCount = 0;
			    	int paramCount = 0;
					for (Field f : fs) 
				    {
						JSONObject conditionInfoParam = new JSONObject();
				    
						f.setAccessible(true);

					    if(f.getAnnotation(ConditionParamArray.class) != null)
					    {
					    	ConditionParamArray annotationParamArray = (ConditionParamArray) f.getAnnotation(ConditionParamArray.class);
					    	
					    	System.out.println(annotationParamArray.name());
					    	conditionInfoParam.put("param_name", annotationParamArray.name());
					    	conditionInfoParam.put("description_de", annotationParamArray.description_de());
					    	conditionInfoParam.put("description_en", annotationParamArray.description_en());
					    	conditionInfoParam.put("type", annotationParamArray.type());
					    	conditionsInfo.put("param_array_" + (paramArrayCount++) , conditionInfoParam);
					    }
					    
					    if(f.getAnnotation(ConditionParam.class) != null)
					    {
					    	ConditionParam annotationParam = (ConditionParam) f.getAnnotation(ConditionParam.class);
					    	
					    	System.out.println(annotationParam.name());
					    	conditionInfoParam.put("param_name", annotationParam.name());
					    	conditionInfoParam.put("description_de", annotationParam.description_de());
					    	conditionInfoParam.put("description_en", annotationParam.description_en());
					    	conditionInfoParam.put("type", annotationParam.type());
					    	conditionsInfo.put("param_" + (paramCount++) , conditionInfoParam);
					    }
					    
				    }
					conditionsInfoArray.put(conditionsInfo);
			    }
			    */
				 
				 
		    }
			String condtionInfoString = new Gson().toJson(conditions);
		    System.out.println(condtionInfoString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(conditionsInfoArray.toString());
		
	}
	
}


