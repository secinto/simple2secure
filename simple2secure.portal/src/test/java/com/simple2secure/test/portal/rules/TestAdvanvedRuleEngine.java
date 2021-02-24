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
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.commons.time.TimeUtils;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.repository.TriggeredRuleHistoryRepository;
import com.simple2secure.portal.rules.PortalRuleEngine;
import com.simple2secure.portal.rules.actions.TemplateActionSendNotification;
import com.simple2secure.portal.rules.conditions.TemplateConditionEmailBlockedWordsInSubject;
import com.simple2secure.portal.utils.RuleUtils;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestAdvanvedRuleEngine 
{
	
	@Autowired
	private PortalRuleEngine portalRuleEngine;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private RuleUtils ruleUtils;

	@Autowired
	TriggeredRuleHistoryRepository triggeredRuleHistoryRepository;

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
		// clearing the database for the notification
		notificationRepository.deleteAll();
		contextId = new ObjectId();
	}

	/**
	 * Method to create a EmailConfig otherwise the TemplateAction send Notification won´t work
	 *
	 * @return id
	 */
	public ObjectId getcreatedEmailConfig() {
		EmailConfiguration emailConfig = new EmailConfiguration(contextId, "", "", "", "", "", "");
		emailConfigurationRepository.deleteAll();
		emailConfigurationRepository.save(emailConfig);
		emailConfig = (emailConfigurationRepository.findByContextId(contextId)).get(0);
		return emailConfig.getId();
	}

	private TemplateAction getNotificationTemplateAction(String text) {
		// an notification will be saved in the database if rule was triggered
		TemplateAction action = new TemplateAction(TemplateActionSendNotification.class.getName(), "general_rules_action_name_send_notification",
				"", RuleFactType.EMAIL, new ArrayList<RuleParam<?>>() {
					private static final long serialVersionUID = 1L;

					{
						add(new RuleParam<>("general_rules_action_param_notification_text", "", text, DataType._STRING));
					}
				}, null);

		return action;
	}

	private TemplateCondition getSubjectWordsTemplateCondition() {
		// preparing condition for blocking words in subject
		TemplateCondition condition = new TemplateCondition(TemplateConditionEmailBlockedWordsInSubject.class.getName(),
				"email_rules_condition_name_find_words_in_subject", "", RuleFactType.EMAIL, null, new ArrayList<RuleParamArray<?>>() {
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
	public void testTriggeringRuleOnlyAfterNTimes() throws Exception {
		// count how often the rule must be triggered before action will be executed
		int N = 3;
		triggeredRuleHistoryRepository.deleteAll();

		// preparing condition for blocking email address
		TemplateCondition condition = getSubjectWordsTemplateCondition();

		String conditionExpression = "A";
		TemplateRule ruleData = new TemplateRule("blocking words in subject rule", "", new ObjectId(), N, conditionExpression,
				Collections.singletonList(condition),
				Collections.singletonList(getNotificationTemplateAction("multiple emails from the same type")));

		// normally the id would be set be the database. But in this ruleDate object is not
		// saved in the db so we have to set the id.
		ruleData.setId(new ObjectId());
		
		Rule rule = ruleUtils.buildRuleFromTemplateRuleWithBeanAndLimit(ruleData, StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH,
				StaticConfigItems.TEMPLATE_ACTIONS_PACKAGE_PATH);
		portalRuleEngine.addRule(rule);
		portalRuleEngine.addFact(contextId);

		for (int count = 1; count <= N; count++) {
			// email which should be checked 5dfa1cc5f8166c3f06ae09c5
			Email email1 = new Email(String.valueOf(count), getcreatedEmailConfig(), count, "WON MONEY",
					String.valueOf(count) + "alice@lottery.com", "text" + String.valueOf(count),
					TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

			portalRuleEngine.addFact(email1);
			portalRuleEngine.checkFacts();
			portalRuleEngine.removeFact(email1.getClass().getName());

			// if database is null the rule has not been triggered
			if (count < N) {
				assertFalse(notificationRepository.findAll() != null, "Rule triggered to Early at the " + count + " time");
			} else {
				assertFalse(notificationRepository.findAll() == null, "Rule did not trigger");
			}
		}
	}

	@Test
	public void testMultipleActions() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, IOException {
		ArrayList<TemplateAction> actions = new ArrayList<>();
		// an notification will be saved in the database if rule was triggered
		actions.add(getNotificationTemplateAction("first"));
		actions.add(getNotificationTemplateAction("second"));
		actions.add(getNotificationTemplateAction("third"));
		String conditionExpression = new String("A");
		TemplateRule ruleData = new TemplateRule("blocking words in subject rule", "", new ObjectId(), 1, conditionExpression,
				Collections.singletonList(getSubjectWordsTemplateCondition()), actions);
		ruleData.setId(new ObjectId());
		
		Rule rule = ruleUtils.buildRuleFromTemplateRuleWithBeanAndLimit(ruleData,StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH,
				StaticConfigItems.TEMPLATE_ACTIONS_PACKAGE_PATH);
		portalRuleEngine.addRule(rule);

		Email email1 = new Email("test", getcreatedEmailConfig(), 1, "WON MONEY", "asdf", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		portalRuleEngine.addFact(email1);
		portalRuleEngine.addFact(contextId);
		portalRuleEngine.checkFacts();

		assertFalse(notificationRepository.findAll().size() != 3, "not every action has been executed");

	}

}
