package com.simple2secure.test.portal.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

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
import com.simple2secure.api.model.RuleParam;
import com.simple2secure.api.model.RuleParamArray;
import com.simple2secure.api.model.TemplateAction;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.commons.time.TimeUtils;
import com.simple2secure.portal.Simple2SecurePortal;
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

	@Test
	public void testConditionBlockedDomain() throws ParseException {
		// clearing the database for the notification
		notificationRepository.deleteAll();

		// email which should be checked
		Email email1 = new Email("1", "5d4ad93028dad11740e9f4b5", 1, "subject", "alice@g00gle.com", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking domain
		TemplateCondition condition = new TemplateCondition("blocked domains", "", "", null, new ArrayList<RuleParamArray<?>>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			{
				add(new RuleParamArray<>("domains", null, null, // new ArrayList<RuleParam<?>>();
						new ArrayList<String>() {
							/**
							 *
							 */
							private static final long serialVersionUID = 1L;

							{
								add("g00gle.com");
							}
						}, DataType._STRING));
			}
		});

		// an notification will be saved in the database if rule was triggered
		TemplateAction action = new TemplateAction("send notification", // text
				"", "", new ArrayList<RuleParam<?>>() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					{
						add(new RuleParam<>("text", "", "", "email came from blocked domain", DataType._STRING));
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
		Email email1 = new Email("1", "5d4ad93028dad11740e9f4b5", 1, "subject", "alice@g00gle.com", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking email address
		TemplateCondition condition = new TemplateCondition("blocked email addresses", "", "", null, new ArrayList<RuleParamArray<?>>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			{
				add(new RuleParamArray<>("email addresses", null, null, new ArrayList<String>() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					{
						add("alice@g00gle.com");
					}
				}, DataType._STRING));
			}
		});

		// an notification will be saved in the database if rule was triggered
		TemplateAction action = new TemplateAction("send notification", // text
				"", "", new ArrayList<RuleParam<?>>() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					{
						add(new RuleParam<>("text", "", "", "email came from blocked address", DataType._STRING));
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
		Email email1 = new Email("1", "5d4ad93028dad11740e9f4b5", 1, "WON MONEY", "alice@lottery.com", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking email address
		TemplateCondition condition = new TemplateCondition("find words in subject", "", "", null, new ArrayList<RuleParamArray<?>>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			{
				add(new RuleParamArray<>("words to find", null, null, new ArrayList<String>() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					{
						add("won");
						add("lottery");
					}
				}, DataType._STRING));
			}
		});

		// an notification will be saved in the database if rule was triggered
		TemplateAction action = new TemplateAction("send notification", // text
				"", "", new ArrayList<RuleParam<?>>() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					{
						add(new RuleParam<>("text", "", "", "email had some blocked words in the subject", DataType._STRING));
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
		Email email1 = new Email("1", "5d4ad93028dad11740e9f4b5", 1, "subject", "alice@lottery.com",
				"You won in the lottery. Just click here ...", TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2019"));

		// preparing condition for blocking email address
		TemplateCondition condition = new TemplateCondition("find words in text", "", "", null, new ArrayList<RuleParamArray<?>>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			{
				add(new RuleParamArray<>("words to find", null, null, new ArrayList<String>() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					{
						add("won");
						add("lottery");
					}
				}, DataType._STRING));
			}
		});

		// an notification will be saved in the database if rule was triggered
		TemplateAction action = new TemplateAction("send notification", // text
				"", "", new ArrayList<RuleParam<?>>() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					{
						add(new RuleParam<>("text", "", "", "email had some blocked words in the text", DataType._STRING));
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
