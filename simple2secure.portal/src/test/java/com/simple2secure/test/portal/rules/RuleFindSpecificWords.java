package com.simple2secure.test.portal.rules;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.EmailAttribute;
import com.simple2secure.api.model.EmailAttributeEnum;
import com.simple2secure.portal.repository.EmailAttributesListRepository;

@Component
@Rule(name = "rule specific words",description = 
             "rule which finds specific words in an email message", priority = 
             2)
public class RuleFindSpecificWords
{
	@Autowired
	EmailAttributesListRepository emailAttributesListRepository;	
	
	private Boolean initialized = false;
	
	private Collection<String> found_words = new ArrayList<String>();
	private Collection<EmailAttribute> forbidden_words;
	
	@PostConstruct
	public void init() 
	{
		// saving words for forbidden words
		if(emailAttributesListRepository.findByAttributeType(
				EmailAttributeEnum.FORBIDDEN_WORDS).isEmpty())
		{
			emailAttributesListRepository.save(new EmailAttribute(
					"LOTTERY",  EmailAttributeEnum.FORBIDDEN_WORDS));
			
			emailAttributesListRepository.save(new EmailAttribute(
					"DOWNLOAD",  EmailAttributeEnum.FORBIDDEN_WORDS));
		}	
		
		forbidden_words = emailAttributesListRepository.findByAttributeType(
				EmailAttributeEnum.FORBIDDEN_WORDS);
		
		initialized = true;
	}
	
    /**
    * Method to check if the text of an email includes specific words.
    * The forbidden words are saved in the database
    * 
    *
    * @param email where the text should be checked
    * @return boolean true if the rule is followed, false otherwise
    *
    */
	@Condition
	public boolean serachingWordsInText(@Fact("com.simple2secure.api.model.Email") Email email)
	{
	    if(!initialized)
	        init();

		
		if(forbidden_words == null)
			return false;
		
		for(EmailAttribute element : forbidden_words)
		{
			if (email.getText().toUpperCase().contains(element.getAttribute()))
				found_words.add(element.getAttribute());
		}
		
		if(found_words.isEmpty())
		  return false;		
		return true;
	}
	
    /**
    * Method which will be executed when a forbidden word has been found
    * 
    *
    * @param email where the forbidden word has been found
    * @return boolean true if the rule is followed, false otherwise
    *
    */
	@Action
	public void found(@Fact("com.simple2secure.api.model.Email") Email email) throws Exception
	{
		if (!found_words.isEmpty())
		{
//			email.setSpam(true);
			StringBuilder spam_info  = new StringBuilder(
					"Found forbidden word(s) in Email text: ");
			for(String forbidden_word : found_words)
			    spam_info.append(forbidden_word + ", ");
//			email.addSpamInfo(spam_info.toString());
			found_words.clear();
		}
	}
}
