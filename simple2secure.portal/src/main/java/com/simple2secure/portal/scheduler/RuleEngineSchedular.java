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

package com.simple2secure.portal.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.rules.PortalRuleEngine;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Class provides scheduled methods to fetch different data which should be used
 * as input for the rule engine, and forwards them to the engine.
 *
 */
@Component
@Slf4j
public class RuleEngineSchedular extends BaseUtilsProvider
{
	
	@Autowired
	PortalRuleEngine portalRuleEngine;

	
	@Scheduled(fixedDelay = 600000)
	public void checkOsQueryReports() throws InterruptedException
	{
		List<OsQueryReport> reports = factsToCheckRepository.findAndRemoveAllUnchecked();
		
		for(OsQueryReport report : reports)
		{
			portalRuleEngine.check(report, contextUtils.getContextIdFromOsQueryReport(report));
		}
	}
	
	@Scheduled(fixedDelay = 600000)
	public void checkNetworkReports() throws InterruptedException
	{
		List<NetworkReport> reports = factsToCheckRepository.findAndRemoveAllUncheckedByFact(RuleFactType.NETWORKREPORT, NetworkReport.class);
		
		for(NetworkReport report : reports)
		{
			portalRuleEngine.check(report, contextUtils.getContextIdFromNetworkReport(report));
		}
	}
	
	@Scheduled(fixedDelay = 600000)
	public void checkTestResults() throws InterruptedException
	{
		List<TestResult> results = factsToCheckRepository.findAndRemoveAllUncheckedByFact(RuleFactType.TESTRESULT, TestResult.class);
		
		for(TestResult result : results)
		{
			portalRuleEngine.check(result, contextUtils.getContextIdFromTestResult(result));
		}
	}
	
	@Scheduled(fixedDelay = 600000)
	public void checkTestSequenceResults() throws InterruptedException
	{
		List<TestSequenceResult> results = factsToCheckRepository.findAndRemoveAllUncheckedByFact(RuleFactType.TESTSEQUENCERESULT, TestSequenceResult.class);
		
		for(TestSequenceResult result : results)
		{
			portalRuleEngine.check(result, contextUtils.getContextIdFromTestSequenceResult(result));
		}
	}
}
