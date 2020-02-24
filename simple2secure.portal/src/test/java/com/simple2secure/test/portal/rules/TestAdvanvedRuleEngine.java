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

package com.simple2secure.test.portal.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import org.jeasy.rules.api.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.api.model.RuleParam;
import com.simple2secure.api.model.RuleParamArray;
import com.simple2secure.api.model.TemplateAction;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.api.model.TriggeredRuleEmail;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.commons.time.TimeUtils;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.EmailTriggeredRuleHistoryRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.repository.RuleConditionsRepository;
import com.simple2secure.portal.rules.EmailRulesEngine;
import com.simple2secure.portal.utils.RuleUtils;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestAdvanvedRuleEngine {
	
	@Autowired
	private EmailRulesEngine emailRulesEngine;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private RuleConditionsRepository ruleConditionsRepository;
	
	@Autowired
	private RuleUtils ruleUtils;
	
	@Autowired
	EmailTriggeredRuleHistoryRepository emailTriggeredRuleHistoryRepository;
	
	@Autowired
	EmailConfigurationRepository emailConfigurationRepository;
	
	/**
	 * Method to create a EmailConfig otherwise the TemplateAction send Notification won´t work
	 * @return 
	 */
	public String createEmailConfig()
	{
		EmailConfiguration emailConfig = new EmailConfiguration("1", "", "", "", "", "", "");
		emailConfigurationRepository.deleteAll();
		emailConfigurationRepository.save(emailConfig);
		return emailConfigurationRepository.findByContextId("1").get(0).id;
		 
	}
	
	private TemplateAction getNotificationTemplateAction()
	{
		// an notification will be saved in the database if rule was triggered
		TemplateAction action = new TemplateAction("email_rules_action_name_send_notification", "", new ArrayList<RuleParam<?>>() {
					private static final long serialVersionUID = 1L;

					{
						add(new RuleParam<>("email_rules_action_param_notification_text","", "email had some blocked words in the subject", DataType._STRING));
					}
				}, null);
		
		return action;
	}
	
	private TemplateCondition getSubjectWordsTemplateCondition()
	{
		// preparing condition for blocking email address
		TemplateCondition condition = new TemplateCondition("email_rules_condition_name_find_words_in_subject", "", null, new ArrayList<RuleParamArray<?>>() {
			private static final long serialVersionUID = 1L;
			{
				add(new RuleParamArray<>("email_rules_condition_paramarray_name_words_to_find", null, new ArrayList<String>() {
					private static final long serialVersionUID = 1L;
					{
						add("won");
						add("lottery");
					}
				}, DataType._STRING));
			}
		});
		
		return condition;
	}
	
	@Test
	public void testConditionFindWordsInSubject() throws ParseException {
		// clearing the database for the notification
		notificationRepository.deleteAll();
		// clear all old rules from last test
		emailRulesEngine.removeAllRules();

		// email which should be checked  5dfa1cc5f8166c3f06ae09c5
		Email email1 = new Email("1", createEmailConfig(), 1, "WON MONEY", "alice@lottery.com", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking email address
		TemplateCondition condition = getSubjectWordsTemplateCondition();

		// an notification will be saved in the database if rule was triggered
		TemplateAction action = getNotificationTemplateAction();

		TemplateRule ruleData = new TemplateRule("blocking words in subject rule", "", "", condition, action);

		try {
			Rule rule = ruleUtils.buildRuleFromTemplateRuleWithBean(ruleData, "com.simple2secure.portal.rules.conditions",
					"com.simple2secure.portal.rules.actions");
			emailRulesEngine.addRule(rule);
			emailRulesEngine.addFact(email1);
			emailRulesEngine.checkFacts();

			// if database is null the rule has not been triggered
			assertFalse(notificationRepository.findAll() == null,
					"Condition \"blocked words in subject\" did not worked. No notification found.");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testSaveIntoEmailRuleTriggeredRepo()
	{
		try {
			emailTriggeredRuleHistoryRepository.deleteAll();
			TriggeredRuleEmail triggeredRule = new TriggeredRuleEmail(new TemplateRule("test", "test", "test", null, null));
			emailTriggeredRuleHistoryRepository.save(triggeredRule);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReadFromEmailRuleTriggeredRepo() {
		try {
			TriggeredRuleEmail triggeredRule = emailTriggeredRuleHistoryRepository.findByRuleName("test");
			assertFalse(triggeredRule == null, "Failed to load triggered rule data from db by name");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTriggeringRuleOnlyAfterNTimes() {
		
		//count how often the rule must be triggered before action will be executed
		int N = 3;
		
		// clearing the database for the notification
		notificationRepository.deleteAll();
		// clear all old rules from last test
		emailRulesEngine.removeAllRules();

		// preparing condition for blocking email address
		TemplateCondition condition = getSubjectWordsTemplateCondition();		
		condition.setParams(
				new ArrayList<RuleParam<?>>() {
					private static final long serialVersionUID = 1L;
					{
						add(new RuleParam<>(AnnotationRuleParam.TYPE_LIMIT, "", N, DataType._INT));
					}
				});
		
		// an notification will be saved in the database if rule was triggered
		TemplateAction action = getNotificationTemplateAction();

		TemplateRule ruleData = new TemplateRule("blocking words in subject rule", "", "", condition, action);

		try {
			Rule rule = ruleUtils.buildRuleFromTemplateRuleWithBean(ruleData, "com.simple2secure.portal.rules.conditions",
					"com.simple2secure.portal.rules.actions");
			emailRulesEngine.addRule(rule);

			for (int count = 1; count <= N; count++)
			{
				// email which should be checked  5dfa1cc5f8166c3f06ae09c5
				Email email1 = new Email(String.valueOf(count), createEmailConfig(), count, "WON MONEY", String.valueOf(count)+"alice@lottery.com", "text" + String.valueOf(count),
						TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

				emailRulesEngine.addFact(email1);
				emailRulesEngine.checkFacts();

				// if database is null the rule has not been triggered
				if(count < N)
					assertFalse(notificationRepository.findAll() != null,
						"Rule triggered to Early at the " + count + " time");
				else 
					assertFalse(notificationRepository.findAll() == null,
							"Rule did not trigger");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	@Test
	public void testLoadConditionsFromPath()
	{
		try {
			Collection<TemplateCondition> conditions;
			conditions = ruleUtils.loadTemplateConditions("com.simple2secure.portal.rules.conditions");
			if(conditions != null && !conditions.isEmpty()) {
				conditions.forEach(ruleConditionsRepository::save);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
