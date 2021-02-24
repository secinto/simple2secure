package com.simple2secure.test.portal.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.RuleParam;
import com.simple2secure.api.model.RuleRegex;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.exceptions.InconsistentDataException;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.repository.OsQueryReportRepository;
import com.simple2secure.portal.repository.RuleRegexRepository;
import com.simple2secure.portal.rules.conditions.ConditionManager;
import com.simple2secure.portal.rules.conditions.TemplateConditionOSQueryReportNumRows;
import com.simple2secure.portal.rules.conditions.TemplateConditionOSQueryReportQuerySearch;
import com.simple2secure.portal.rules.conditions.TemplateConditionOSQueryReportResultSearch;
import com.simple2secure.portal.utils.RuleUtils;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestOSQueryReportRuleEngine
{

	@Autowired
	OsQueryReportRepository osQueryReportRep;

	@Autowired
	RuleUtils ruleUtils;

	@Autowired
	RuleRegexRepository ruleRegexRepository;

	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	

	@Test
	void testConditionCountRows() throws ClassNotFoundException, IOException, InconsistentDataException, InstantiationException, IllegalAccessException 
	{

		List<OsQueryReport> reports = osQueryReportRep.findAll();

		assertFalse("No reports saved on the system: Test could not be started.", (reports == null || reports.size() == 0));

		OsQueryReport report = reports.get(0);
		int resultRows = JSONUtils.fromString(report.getQueryResult()).size();

		ruleUtils.loadAndSaveTemplateConditions();
		TemplateCondition conditionDate1 = new TemplateCondition(TemplateConditionOSQueryReportNumRows.class.getName(),
				"os_query_report_condition_name_count_row", "", RuleFactType.OSQUERYREPORT,
				Collections.singletonList(new RuleParam<>("os_query_report_condition_param_name_threshold", "", resultRows, DataType._INT)),
				null);

		TemplateCondition conditionDate2 = new TemplateCondition(TemplateConditionOSQueryReportNumRows.class.getName(),
				"os_query_report_condition_name_count_row", "", RuleFactType.OSQUERYREPORT,
				Collections.singletonList(new RuleParam<>("os_query_report_condition_param_name_threshold", "", resultRows - 1, DataType._INT)),
				null);

		Condition condition1 = ruleUtils.buildConditionFromTemplateCondition(conditionDate1,
				StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH, "");
		Condition condition2 = ruleUtils.buildConditionFromTemplateCondition(conditionDate2,
				StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH, "");

		Facts facts = new Facts();
		facts.put(report.getClass().getName(), report);
		assertFalse("count rows failed", condition1.evaluate(facts));
		assertFalse("count rows failed", !condition2.evaluate(facts));


	}
	
	@Test
	void testConditionReportResultSearch() throws InconsistentDataException, ClassNotFoundException,
	InstantiationException, IllegalAccessException, IOException
	{
		
		ruleRegexRepository.deleteAll();
		Facts facts = new Facts();
		
		// test which finds specific word:
		RuleRegex specificRegex = new RuleRegex("specificRegex", "", new ObjectId(), "specificText");
		ruleRegexRepository.save(specificRegex);
		
		// condition which searches the result of the OsQueryReport with the given regex
		TemplateCondition conditionData = new TemplateCondition(TemplateConditionOSQueryReportResultSearch.class.getName(),
				"os_query_report_condition_name_result_parser", "",
				RuleFactType.OSQUERYREPORT,
				Collections.singletonList(new RuleParam<>("os_query_report_condition_param_name_regex", "", "specificRegex", DataType._STRING)),
				null);
		
		// some data for testing the condition
		OsQueryReport report = new OsQueryReport(
				new ObjectId(),
				"select something from something",
				"[{\"atime\":\"2020-04-26 08:41:33\",\"directory\":\"C:\\\\ProgramData\\\\Microsoft\\\\Windows\\\\Start Menu\\\\Programs\"}]",
				new Date(), false);
		
		Condition condition = ruleUtils.buildConditionFromTemplateCondition(conditionData, StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH, "testRule");
		autowireCapableBeanFactory.autowireBean(condition);
		
		facts.put(report.getClass().getName(), report);
		assertFalse("search only for the word \"specificText\": has to be false", condition.evaluate(facts));
		
		
		
		
		// test with a regex which should find always a match
		RuleRegex findAllRegex = new RuleRegex("findAllRegex", "", new ObjectId(), "(.*?)");
		ruleRegexRepository.save(findAllRegex);
		
		conditionData.setParams(Collections.singletonList(new RuleParam<>("os_query_report_condition_param_name_regex", "", "findAllRegex", DataType._STRING)));
		condition = ruleUtils.buildConditionFromTemplateCondition(conditionData, StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH, "testRule");
		autowireCapableBeanFactory.autowireBean(condition);
		assertTrue("should always be true, because the regex includes every combination of characters", condition.evaluate(facts));
		
		
		// test with a regex which should check if uptime is greater than xxx seconds
		// some data for testing the condition
		
		
		OsQueryReport reportUptime = new OsQueryReport(
				new ObjectId(),
				"select * from uptime;",
				"[{\"days\":\"0\",\"hours\":\"2\",\"minutes\":\"22\",\"seconds\":\"3\",\"total_seconds\":\"8523\"}]",
				new Date(), false);
		
		facts.put(report.getClass().getName(), reportUptime);
		
		RuleRegex selectUptimeRegex = new RuleRegex("selectUptimeRegex", "", new ObjectId(), ".*?select \\* from uptime;.*?");
		ruleRegexRepository.save(selectUptimeRegex);
		RuleRegex uptimeGreater2h = new RuleRegex("uptimeGreater2h", "", new ObjectId(), ".*?\"hours\":\"[23456]\".*?");
		ruleRegexRepository.save(uptimeGreater2h);
		
		TemplateCondition conditionDataFindUptime = new TemplateCondition(TemplateConditionOSQueryReportQuerySearch.class.getName(),
				"os_query_report_condition_name_query_parser", "",
				RuleFactType.OSQUERYREPORT,
				Collections.singletonList(new RuleParam<>("os_query_report_condition_param_name_regex", "", "selectUptimeRegex", DataType._STRING)),
				null);
		
		TemplateCondition conditionDataUptimeBigger2h = new TemplateCondition(TemplateConditionOSQueryReportResultSearch.class.getName(),
				"os_query_report_condition_name_result_parser", "",
				RuleFactType.OSQUERYREPORT,
				Collections.singletonList(new RuleParam<>("os_query_report_condition_param_name_regex", "", "uptimeGreater2h", DataType._STRING)),
				null);
		
		
		List<Condition> conditions = new ArrayList<Condition>();
		condition = ruleUtils.buildConditionFromTemplateCondition(conditionDataFindUptime, StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH, "testRule");
		autowireCapableBeanFactory.autowireBean(condition);
		conditions.add(condition);
		
		condition = ruleUtils.buildConditionFromTemplateCondition(conditionDataUptimeBigger2h, StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH, "testRule");
		autowireCapableBeanFactory.autowireBean(condition);
		conditions.add(condition);
		
		// only if the osquery report has a select uptime query and the result is that the uptime is between 4 and 6 hours it
		// will be evaluated as true
		ConditionManager conditionManager = new ConditionManager(conditions, "A & B");
		autowireCapableBeanFactory.autowireBean(conditionManager);
		
		
		boolean result = conditionManager.evaluate(facts);
		assertTrue("The result should be true because both conditions are fulfilled!", result);		
	
	}
}
