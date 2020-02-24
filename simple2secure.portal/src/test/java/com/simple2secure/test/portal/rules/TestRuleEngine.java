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

import org.jeasy.rules.api.Rule;
import org.junit.jupiter.api.BeforeAll;
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
import com.simple2secure.commons.time.TimeUtils;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.rules.EmailRulesEngine;
import com.simple2secure.portal.utils.RuleUtils;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestRuleEngine {

	@Autowired
	private EmailRulesEngine emailRulesEngine;

	@Autowired
	RuleUtils ruleUtils;

	@Autowired
	NotificationRepository notificationRepository;
	
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

	@Test
	public void testConditionBlockedDomain() throws ParseException {
		// clearing the database for the notification
		notificationRepository.deleteAll();

		// email which should be checked
		Email email1 = new Email("1", createEmailConfig(), 1, "subject", "<alice@g00gle.com>", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking domain
		TemplateCondition condition = new TemplateCondition("email_rules_condition_name_blocked_domains", "", new ArrayList<RuleParam<?>>(), new ArrayList<RuleParamArray<?>>() {
			private static final long serialVersionUID = 1L;

			{
				add(new RuleParamArray<>("email_rules_condition_param_name_blocked_domains", null, // new ArrayList<RuleParam<?>>();
						new ArrayList<String>() {
							private static final long serialVersionUID = 1L;

							{
								add("g00gle.com");
							}
						}, DataType._STRING));
			}
		});

		// an notification will be saved in the database if rule was triggered
		TemplateAction action = new TemplateAction("email_rules_action_name_send_notification", "", new ArrayList<RuleParam<?>>() {
					private static final long serialVersionUID = 1L;

					{
						add(new RuleParam<>("email_rules_action_param_notification_text", "", "email came from blocked domain", DataType._STRING));
					}
				}, null);

		TemplateRule ruleData = new TemplateRule("blocking domain rule", "", "", condition, action);

		try {
			Rule rule = ruleUtils.buildRuleFromTemplateRuleWithBean(ruleData, "com.simple2secure.portal.rules.conditions",
					"com.simple2secure.portal.rules.actions");
			emailRulesEngine.addRule(rule);
			emailRulesEngine.addFact(email1);
			emailRulesEngine.checkFacts();

			assertFalse(notificationRepository.findAll() == null, "Condition \"blocked domains\" did not worked. No notification found.");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
	}

	@Test
	public void testConditionBlockedEmail() throws ParseException {
		// clearing the database for the notification
		notificationRepository.deleteAll();

		// email which should be checked
		Email email1 = new Email("1", createEmailConfig(), 1, "subject", "<alice@g00gle.com>", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking email address
		TemplateCondition condition = new TemplateCondition("email_rules_condition_name_blocked_email_addresses", "", new ArrayList<RuleParam<?>>(), new ArrayList<RuleParamArray<?>>() {
			private static final long serialVersionUID = 1L;

			{
				add(new RuleParamArray<>("email_rules_condition_paramarray_name_email_addresses", null, new ArrayList<String>() {
					private static final long serialVersionUID = 1L;

					{
						add("alice@g00gle.com");
					}
				}, DataType._STRING));
			}
		});

		// an notification will be saved in the database if rule was triggered
		TemplateAction action = new TemplateAction("email_rules_action_name_send_notification", "", new ArrayList<RuleParam<?>>() {
					private static final long serialVersionUID = 1L;

					{
						add(new RuleParam<>("email_rules_action_param_notification_text", "", "email came from blocked address", DataType._STRING));
					}
				}, null);

		TemplateRule ruleData = new TemplateRule("blocking email address rule", "", "", condition, action);

		try {
			Rule rule = ruleUtils.buildRuleFromTemplateRuleWithBean(ruleData, "com.simple2secure.portal.rules.conditions",
					"com.simple2secure.portal.rules.actions");
			emailRulesEngine.addRule(rule);
			emailRulesEngine.addFact(email1);
			emailRulesEngine.checkFacts();

			// if database is null the rule has not been triggered
			assertFalse(notificationRepository.findAll() == null, "Condition \"blocked email addresses\" did not worked. No notification found.");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
	}

	@Test
	public void testConditionFindWordsInSubject() throws ParseException {
		// clearing the database for the notification
		notificationRepository.deleteAll();

		// email which should be checked
		Email email1 = new Email("1", createEmailConfig(), 1, "WON MONEY", "<alice@lottery.com>", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking email address
		TemplateCondition condition = new TemplateCondition("email_rules_condition_name_find_words_in_subject", "", new ArrayList<RuleParam<?>>(), new ArrayList<RuleParamArray<?>>() {
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

		// an notification will be saved in the database if rule was triggered
		TemplateAction action = new TemplateAction("email_rules_action_name_send_notification", "", new ArrayList<RuleParam<?>>() {
					private static final long serialVersionUID = 1L;

					{
						add(new RuleParam<>("email_rules_action_param_notification_text", "", "email had some blocked words in the subject", DataType._STRING));
					}
				}, null);

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
	public void testConditionFindWordsInText() throws ParseException {
		// clearing the database for the notification
		notificationRepository.deleteAll();

		// email which should be checked
		Email email1 = new Email("1", createEmailConfig(), 1, "subject", "<alice@lottery.com>",
				"You won in the lottery. Just click here ...", TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking email address
		TemplateCondition condition = new TemplateCondition("email_rules_condition_name_find_words_in_text", "", new ArrayList<RuleParam<?>>(), new ArrayList<RuleParamArray<?>>() {
			private static final long serialVersionUID = 1L;

			{
				add(new RuleParamArray<>("email_rules_condition_paramarray_name_words_to_find_in_text", null, new ArrayList<String>() {
					private static final long serialVersionUID = 1L;

					{
						add("won");
						add("lottery");
					}
				}, DataType._STRING));
			}
		});

		// an notification will be saved in the database if rule was triggered
		TemplateAction action = new TemplateAction("email_rules_action_name_send_notification", "", new ArrayList<RuleParam<?>>() {
					private static final long serialVersionUID = 1L;

					{
						add(new RuleParam<>("email_rules_action_param_notification_text", "", "email had some blocked words in the text", DataType._STRING));
					}
				}, null);

		TemplateRule ruleData = new TemplateRule("blocking words in text rule", "", "", condition, action);

		try {
			Rule rule = ruleUtils.buildRuleFromTemplateRuleWithBean(ruleData, "com.simple2secure.portal.rules.conditions",
					"com.simple2secure.portal.rules.actions");
			emailRulesEngine.addRule(rule);
			emailRulesEngine.addFact(email1);
			emailRulesEngine.checkFacts();

			// if database is null the rule has not been triggered
			assertFalse(notificationRepository.findAll() == null, "Condition \"blocked words in text\" did not worked. No notification found.");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
	}
}
