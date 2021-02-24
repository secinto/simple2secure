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

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.RuleRegex;
import com.simple2secure.commons.exceptions.InconsistentDataException;
import com.simple2secure.commons.messages.Message;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.portal.repository.RuleRegexRepository;

@AnnotationCondition(
		name_tag = "os_query_report_condition_name_query_parser",
		description_tag = "os_query_report_condition_description_query_parser",
		fact_type = RuleFactType.OSQUERYREPORT)
public class TemplateConditionOSQueryReportQuerySearch extends AbstractPortalCondition<OsQueryReport>
{

	@Autowired
	RuleRegexRepository ruleRegexRepository;
	
	@AnnotationRuleParam(
			name_tag = "os_query_report_condition_param_name_regex",
			description_tag = "os_query_report_condition_param_description_regex",
			type = DataType._STRING)
	String regexName;
	
	@Override
	protected boolean condition(OsQueryReport osQueryReport, ObjectId contextId) {
		RuleRegex ruleRegex = ruleRegexRepository.findByName(regexName);
		if(ruleRegex == null)
			throw new InconsistentDataException(new Message("","No regex found in the database with the name: " + regexName));
		
		String regex = ruleRegex.getRegex();
		
		boolean result = false;
		
		String query = osQueryReport.getQuery();
		result = query.matches(regex);
		
		return result;
	}

}
