/**
*********************************************************************
*   simple2secure is a cyber risk and information security platform.
*   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
*********************************************************************
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU Affero General Public License as
*   published by the Free Software Foundation, either version 3 of the
*   License, or (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*   GNU Affero General Public License for more details.
*
*   You should have received a copy of the GNU Affero General Public License
*   along with this program.  If not, see <https://www.gnu.org/licenses/>.
*  
 *********************************************************************
*/

package com.simple2secure.api.model;

import java.util.ArrayList;
import java.util.List;

import com.simple2secure.api.dbo.GenericDBObject;

import lombok.Getter;

@Getter
public class TriggeredRuleEmail extends GenericDBObject {
	
	private static final long serialVersionUID = -5472051873781398191L;
	private TemplateRule rule;
	private List<Email> emails;	
	
	public TriggeredRuleEmail() {
		
	}
	
	public TriggeredRuleEmail(TemplateRule rule, List<Email> emails) {
		this.rule = rule;
		this.emails = emails;
	}
	
	public TriggeredRuleEmail(TemplateRule rule)
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
