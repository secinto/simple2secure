package com.simple2secure.test.portal.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import org.bson.types.ObjectId;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.RuleParamArray;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.time.TimeUtils;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.rules.conditions.ConditionManager;
import com.simple2secure.portal.rules.conditions.TemplateConditionEmailBlockedWordsInSubject;
import com.simple2secure.portal.utils.RuleUtils;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestMultipleCondition {

	// @Autowired
	private RuleUtils ruleUtils = new RuleUtils();

	// Helper methods for testing
	// =======================================================================================================
	private Condition getSubjectWordsCondition(String word) throws Exception {
		// preparing condition for blocking words in subject
		TemplateCondition condition = new TemplateCondition(TemplateConditionEmailBlockedWordsInSubject.class.getName(),
				"email_rules_condition_name_find_words_in_subject", "", RuleFactType.EMAIL, null, new ArrayList<RuleParamArray<?>>() {
					private static final long serialVersionUID = 1L;
					{
						add(new RuleParamArray<>("email_rules_condition_paramarray_name_words_to_find", null, new ArrayList<String>() {
							private static final long serialVersionUID = 1L;
							{
								add(word);
							}
						}, DataType._STRING));
					}
				});

		return ruleUtils.buildConditionFromTemplateCondition(condition, StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH, "rule name");
	}

	// =======================================================================================================
	// =======================================================================================================

	// Actual test methods
	// =======================================================================================================

	@Test
	public void testParserLibrary() throws ParseException {
		String expression = new String("(A & B) | C");

		ArrayList<Boolean> results = new ArrayList<>();
		results.add(Boolean.FALSE); // A = true;
		results.add(Boolean.TRUE); // B = false;
		results.add(Boolean.FALSE); // C = true;

		try {
			Expression<String> resultExpression = ExprParser.parse(expression);
			for (int i = 0; i < results.size(); i++) {
				resultExpression = RuleSet.assign(resultExpression, Collections.singletonMap(String.valueOf((char) (i + 65)), results.get(i)));
			}
			assertFalse(!resultExpression.equals(Literal.getFalse()), "Failed: Library Test!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMultipleConditon() throws Exception {
		Facts facts = new Facts();
		ConditionManager manager;
		boolean result;
		Email email;

		ArrayList<Condition> conditions = new ArrayList<>();
		conditions.add(getSubjectWordsCondition("lottery")); // Condition A
		conditions.add(getSubjectWordsCondition("won")); // Condition B
		conditions.add(getSubjectWordsCondition("money")); // Condition C
		conditions.add(getSubjectWordsCondition("$$$")); // Condition D

		int count = 1;
		String conditionExpression = new String("A & B & C & D");
		manager = new ConditionManager(conditions, conditionExpression);
		email = new Email(String.valueOf(count), new ObjectId(), count, "WON MONEY", "alice@lottery.com", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2020"));
		facts.put(email.getClass().getName(), email);
		result = manager.evaluate(facts);
		facts.remove(email.getClass().getName());
		assertFalse(result, "Test " + count + ": Failed: Evaluation of first rule should have been false!");

		count++;
		conditionExpression = new String("A & B & C & D");
		manager = new ConditionManager(conditions, conditionExpression);
		email = new Email(String.valueOf(count), new ObjectId(), count, "WON MONEY $$$ lottery", "alice@lottery.com",
				"text", TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2020"));
		facts.put(email.getClass().getName(), email);
		result = manager.evaluate(facts);
		facts.remove(email.getClass().getName());
		assertFalse(!result, "Test " + count + ": Failed: Evaluation of second rule should have been true!");

		count++;
		conditionExpression = new String("(A | B) & (C | D)");
		manager = new ConditionManager(conditions, conditionExpression);
		email = new Email(String.valueOf(count), new ObjectId(), count, "WON money", "alice@lottery.com", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2020"));
		facts.put(email.getClass().getName(), email);
		result = manager.evaluate(facts);
		facts.remove(email.getClass().getName());
		assertFalse(!result, "Test" + count + "Failed: Evaluation of third rule should have been true!");

		count++;
		conditionExpression = new String("(A | B | C | D)");
		manager = new ConditionManager(conditions, conditionExpression);
		email = new Email(String.valueOf(count), new ObjectId(), count, "$$$", "alice@lottery.com", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2020"));
		facts.put(email.getClass().getName(), email);
		result = manager.evaluate(facts);
		facts.remove(email.getClass().getName());
		assertFalse(!result, "Test " + count + ": Failed: Evaluation of fourth rule should have been true!");

		count++;
		conditionExpression = new String("(A | B | (!C & D))");
		manager = new ConditionManager(conditions, conditionExpression);
		email = new Email(String.valueOf(count), new ObjectId(), count, "$$$", "alice@lottery.com", "text",
				TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Mon Aug 19 10:00:00 CEST 2020"));
		facts.put(email.getClass().getName(), email);
		result = manager.evaluate(facts);
		facts.remove(email.getClass().getName());
		assertFalse(!result, "Test " + count + ": Failed: Evaluation of fifth rule should have been false!");
	}

}
