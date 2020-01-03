package com.simple2secure.api.model;

import java.util.ArrayList;
import java.util.List;

import com.simple2secure.api.dbo.GenericDBObject;

import lombok.Getter;

@Getter
public class TriggeredRule extends GenericDBObject {
	
	private static final long serialVersionUID = -5472051873781398191L;
	private TemplateRule rule;
	private List<Email> emails;	
	
	public TriggeredRule() {
		
	}
	
	public TriggeredRule(TemplateRule rule, List<Email> emails) {
		this.rule = rule;
		this.emails = emails;
	}
	
	public TriggeredRule(TemplateRule rule)
	{
		this.rule = rule;
		emails = new ArrayList<>();
	}
	
	
	public int getTriggeredEmailCount()
	{
		return emails.size();
	}
	
	public void addMail(Email email)
	{
		emails.add(email);
	}
}
