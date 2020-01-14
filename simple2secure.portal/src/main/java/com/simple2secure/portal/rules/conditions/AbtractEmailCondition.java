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

package com.simple2secure.portal.rules.conditions;

import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.api.model.TriggeredRuleEmail;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.commons.rules.annotations.RuleName;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.EmailTriggeredRuleHistoryRepository;

import lombok.Setter;

@Setter
public abstract class AbtractEmailCondition implements Condition {

	@Autowired
	EmailTriggeredRuleHistoryRepository emailTriggeredRuleHistoryRepository;
	
	@RuleName()
	protected String ruleName;
	
	@AnnotationRuleParam(
			name = AnnotationRuleParam.TYPE_LIMIT,
			description_de = "Die Regel wird erst bei der n ten Mail des gleichen Typs ausgelöst",
			description_en = "The rule will only be triggered after n mails of the same type",
			type = DataType._INT)
	private int typeLimit;
	
	@Override
	public boolean evaluate(Facts facts) {
		boolean result = condition(facts.get("com.simple2secure.api.model.Email"));
		
		if (!result) {
			return false;			
		}
		else {
			if (typeLimit == 1)
				return result;
			
			Email email = facts.get("com.simple2secure.api.model.Email");	
			
			TriggeredRuleEmail triggeredRule = emailTriggeredRuleHistoryRepository.findByRuleName(ruleName);
			
			if(triggeredRule != null) {
				triggeredRule.getEmails().add(email);
				try {
					emailTriggeredRuleHistoryRepository.update(triggeredRule);
				} catch (ItemNotFoundRepositoryException e) {
					e.printStackTrace();
				}
			}
			else {
				TemplateRule templateRule = new TemplateRule(ruleName,
						"description is not implemented", //TODO:
						email.getConfigId(),
						null, null);
				triggeredRule = new TriggeredRuleEmail(templateRule);
				triggeredRule.addMail(email);
				emailTriggeredRuleHistoryRepository.save(triggeredRule);
			}
			
			if (triggeredRule.getTriggeredEmailCount() >= typeLimit)
				return true;
			else
				return false;
		}
	}

	protected abstract boolean condition(Email email);
	
}
