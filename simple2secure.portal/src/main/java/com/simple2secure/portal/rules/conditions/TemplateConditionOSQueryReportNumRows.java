package com.simple2secure.portal.rules.conditions;

import org.bson.types.ObjectId;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;

@AnnotationCondition(
		name_tag = "os_query_report_condition_name_count_row",
		description_tag = "os_query_report_condition_description_count_row",
		fact_type = RuleFactType.OSQUERYREPORT)
public class TemplateConditionOSQueryReportNumRows extends AbstractPortalCondition<OsQueryReport>
{
	@AnnotationRuleParam(
			name_tag = "os_query_report_condition_param_name_threshold",
			description_tag = "os_query_report_condition_param_description_threshold",
			type = DataType._INT)
	private int threshold;

	@Override
	protected boolean condition(OsQueryReport osQueryReport, ObjectId contextId) {
		if (osQueryReport == null) {
			return false;
		}
		try {
			int resultRows = JSONUtils.fromString(osQueryReport.getQueryResult()).size();
			if (resultRows > threshold) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
