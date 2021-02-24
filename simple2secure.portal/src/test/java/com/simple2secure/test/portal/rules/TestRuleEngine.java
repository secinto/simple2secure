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
import java.util.Collections;

import org.bson.types.ObjectId;
import org.jeasy.rules.api.Rule;
import org.junit.jupiter.api.BeforeEach;
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
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.RuleParam;
import com.simple2secure.api.model.RuleParamArray;
import com.simple2secure.api.model.TemplateAction;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.time.TimeUtils;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.rules.PortalRuleEngine;
import com.simple2secure.portal.rules.actions.TemplateActionSendNotification;
import com.simple2secure.portal.rules.conditions.TemplateConditionEmailBlockedDomains;
import com.simple2secure.portal.rules.conditions.TemplateConditionEmailBlockedAddresses;
import com.simple2secure.portal.rules.conditions.TemplateConditionEmailBlockedWordsInSubject;
import com.simple2secure.portal.utils.RuleUtils;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestRuleEngine 
{
	
	@Autowired
	private PortalRuleEngine portalRuleEngine;

	@Autowired
	RuleUtils ruleUtils;

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	EmailConfigurationRepository emailConfigurationRepository;
	
	private ObjectId contextId;

	@BeforeEach
	private void clearRuleEngine() {
		// clear all old rules from last test
		portalRuleEngine.removeAllRules();
	}

	@BeforeEach
	private void clearNotificationRepo() {
		new TemplateRule();
		// clearing the database for the notification
		notificationRepository.deleteAll();
		contextId = new ObjectId();
	}

	/**
	 * Method to create a EmailConfig otherwise the TemplateAction send Notification won´t work
	 *
	 * @return
	 */
	public ObjectId getcreatedEmailConfig() {
		EmailConfiguration emailConfig = new EmailConfiguration(contextId, "", "", "", "", "", "");
		emailConfigurationRepository.deleteAll();
		emailConfigurationRepository.save(emailConfig);
		emailConfig = (emailConfigurationRepository.findByContextId(contextId)).get(0);
		return emailConfig.getId();
	}

	private TemplateAction createSendNotificationAction(String text) {
		// an notification will be saved in the database if rule was triggered
		TemplateAction action = new TemplateAction(TemplateActionSendNotification.class.getName(), "general_rules_action_name_send_notification",
				"", RuleFactType.EMAIL,
				Collections.singletonList(new RuleParam<>("general_rules_action_param_notification_text", "", text, DataType._STRING)), null);

		return action;
	}

	@Test
	public void testConditionBlockedDomain() throws ParseException {

		// email which should be checked
		Email email1 = new Email("1", getcreatedEmailConfig(), 1, "subject", "<alice@g00gle.com>", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking domain
		TemplateCondition condition = new TemplateCondition(TemplateConditionEmailBlockedDomains.class.getName(),
				"email_rules_condition_name_blocked_domains", "", RuleFactType.EMAIL, null, new ArrayList<RuleParamArray<?>>() {
					private static final long serialVersionUID = 1L;

					{
						add(new RuleParamArray<>("email_rules_condition_param_name_blocked_domains", null, new ArrayList<String>() {
							private static final long serialVersionUID = 1L;

							{
								add("g00gle.com");
							}
						}, DataType._STRING));
					}
				});

		ArrayList<TemplateAction> actions = new ArrayList<>();
		actions.add(createSendNotificationAction("email came from blocked domain"));

		String conditionExpression = new String("A");
		TemplateRule ruleData = new TemplateRule("blocking domain rule", "", new ObjectId(), 1, conditionExpression,
				Collections.singletonList(condition), actions);
		ruleData.setId(new ObjectId());

		try {
			Rule rule = ruleUtils.buildRuleFromTemplateRuleWithBeanAndLimit(ruleData, StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH,
					StaticConfigItems.TEMPLATE_ACTIONS_PACKAGE_PATH);
			portalRuleEngine.addRule(rule);
			portalRuleEngine.addFact(email1);
			portalRuleEngine.addFact(contextId);
			portalRuleEngine.checkFacts();

			assertFalse(notificationRepository.findAll() == null, "Condition \"blocked domains\" did not worked. No notification found.");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
	}

	@Test
	public void testConditionBlockedEmail() throws ParseException {

		// email which should be checked
		Email email1 = new Email("1", getcreatedEmailConfig(), 1, "subject", "<alice@g00gle.com>", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking email address
		TemplateCondition condition = new TemplateCondition(TemplateConditionEmailBlockedAddresses.class.getName(),
				"email_rules_condition_name_blocked_email_addresses", "", RuleFactType.EMAIL, new ArrayList<RuleParam<?>>(),
				new ArrayList<RuleParamArray<?>>() {
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

		ArrayList<TemplateAction> actions = new ArrayList<>();
		actions.add(createSendNotificationAction("email came from blocked address"));

		String conditionExpression = new String("A");
		TemplateRule ruleData = new TemplateRule("blocking email address rule", "", new ObjectId(), 1, conditionExpression,
				Collections.singletonList(condition), actions);
		ruleData.setId(new ObjectId());

		try {
			Rule rule = ruleUtils.buildRuleFromTemplateRuleWithBeanAndLimit(ruleData, StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH,
					StaticConfigItems.TEMPLATE_ACTIONS_PACKAGE_PATH);
			portalRuleEngine.addRule(rule);
			portalRuleEngine.addFact(email1);
			portalRuleEngine.addFact(contextId);
			portalRuleEngine.checkFacts();

			// if database is null the rule has not been triggered
			assertFalse(notificationRepository.findAll() == null, "Condition \"blocked email addresses\" did not worked. No notification found.");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
	}

	@Test
	public void testConditionFindWordsInSubject() throws ParseException {

		// email which should be checked
		Email email1 = new Email("1", getcreatedEmailConfig(), 1, "WON MONEY", "<alice@lottery.com>", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking words in subject
		TemplateCondition condition = new TemplateCondition(TemplateConditionEmailBlockedWordsInSubject.class.getName(),
				"email_rules_condition_name_find_words_in_subject", "", RuleFactType.EMAIL, new ArrayList<RuleParam<?>>(),
				new ArrayList<RuleParamArray<?>>() {
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

		ArrayList<TemplateAction> actions = new ArrayList<>();
		actions.add(createSendNotificationAction("email had some blocked words in the subject"));

		String conditionExpression = new String("A");
		TemplateRule ruleData = new TemplateRule("blocking words in subject rule", "", new ObjectId(), 1, conditionExpression,
				Collections.singletonList(condition), actions);
		ruleData.setId(new ObjectId());

		try {
			Rule rule = ruleUtils.buildRuleFromTemplateRuleWithBeanAndLimit(ruleData, StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH,
					StaticConfigItems.TEMPLATE_ACTIONS_PACKAGE_PATH);
			portalRuleEngine.addRule(rule);
			portalRuleEngine.addFact(email1);
			portalRuleEngine.addFact(contextId);
			portalRuleEngine.checkFacts();

			// if database is null the rule has not been triggered
			assertFalse(notificationRepository.findAll() == null,
					"Condition \"blocked words in subject\" did not worked. No notification found.");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
	}

	@Test
	public void testConditionFindWordsInText() throws ParseException {

		// email which should be checked
		Email email1 = new Email("1", getcreatedEmailConfig(), 1, "subject", "<alice@lottery.com>", "You won in the lottery. Just click here ...",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking words in text
		TemplateCondition condition = new TemplateCondition(TemplateConditionEmailBlockedWordsInSubject.class.getName(),
				"email_rules_condition_name_find_words_in_text", "", RuleFactType.EMAIL, new ArrayList<RuleParam<?>>(),
				new ArrayList<RuleParamArray<?>>() {
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

		ArrayList<TemplateAction> actions = new ArrayList<>();
		actions.add(createSendNotificationAction("email had some blocked words in the text"));

		String conditionExpression = new String("A");
		TemplateRule ruleData = new TemplateRule("blocking words in text rule", "", new ObjectId(), 1, conditionExpression,
				Collections.singletonList(condition), actions);
		ruleData.setId(new ObjectId());

		try {
			Rule rule = ruleUtils.buildRuleFromTemplateRuleWithBeanAndLimit(ruleData, StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH,
					StaticConfigItems.TEMPLATE_ACTIONS_PACKAGE_PATH);
			portalRuleEngine.addRule(rule);
			portalRuleEngine.addFact(email1);
			portalRuleEngine.addFact(contextId);
			portalRuleEngine.checkFacts();

			// if database is null the rule has not been triggered
			assertFalse(notificationRepository.findAll() == null, "Condition \"blocked words in text\" did not worked. No notification found.");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
	}
}
