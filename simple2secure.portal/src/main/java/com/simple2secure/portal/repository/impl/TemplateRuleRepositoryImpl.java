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

package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.QueryBuilder;
import com.mongodb.client.model.Projections;
import com.mongodb.operation.CountOperation;
import com.simple2secure.api.dto.TemplateRulesDTO;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.RuleDeviceMappingRepository;
import com.simple2secure.portal.repository.RuleEmailConfigMappingRepository;
import com.simple2secure.portal.repository.TemplateRuleFactTypeMappingRepository;
import com.simple2secure.portal.repository.TemplateRuleRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class TemplateRuleRepositoryImpl extends TemplateRuleRepository {


	@Autowired
	PortalUtils portalUtils;
	
	@Autowired
	TemplateRuleFactTypeMappingRepository factRuleMapping;
	
	@Autowired
	RuleEmailConfigMappingRepository ruleEmailConfigMappingRepository;
	
	@Autowired
	RuleDeviceMappingRepository ruleDeviceMappingRepository;
	
	@PostConstruct
	public void init() {
		super.collectionName = "templateRule";
		super.className = TemplateRule.class;
	}

	@Override
	public List<TemplateRule> findByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextID").is(contextId));
		return mongoTemplate.find(query, TemplateRule.class);
	}

	@Override
	public TemplateRule findByContextIdAndRuleId(ObjectId contextId, ObjectId ruleId) {
		Query query = new Query(Criteria.where("contextID").is(contextId)
				.and("_id").is(ruleId));
		TemplateRule templateRule = mongoTemplate.findOne(query, TemplateRule.class);
		return templateRule;
	}

	@Override
	public TemplateRule findByContextIdAndRuleName(ObjectId contextId, String name) {
		Query query = new Query(Criteria.where("name").is(name).and("contextID").is(contextId));
		return mongoTemplate.findOne(query, TemplateRule.class);
	}

	@Override
	public List<TemplateRule> findByContextIdAndFactType(ObjectId contextID, RuleFactType type) {
		List<ObjectId> ruleIds = factRuleMapping.findRuleIdsByContextAndFactType(contextID, type);
		
		List<TemplateRule> rules = new ArrayList<TemplateRule>();
		
		ruleIds.forEach(ruleId -> {
			rules.add(findByContextIdAndRuleId(contextID, ruleId));
		});
		
		return rules;
	}
	
	@Override
	public void deleteTemplateRuleWithMapping(ObjectId contextId, ObjectId ruleId, RuleFactType type) {
		TemplateRule ruleToDelete = findByContextIdAndRuleId(contextId, ruleId);
		
		switch (type) {
			case EMAIL:
			{
				ruleEmailConfigMappingRepository.deleteByRuleId(contextId, ruleId);
				break;
			}
			
			case OSQUERYREPORT:
			case NETWORKREPORT:
			case TESTRESULT:
			case TESTSEQUENCERESULT:	
			{
				ruleDeviceMappingRepository.deleteByRuleId(contextId, ruleId);
				break;
			}
			
			case GENERAL:
				break;
			default:
				break;	
		}
		
		mongoTemplate.remove(ruleToDelete);
	}

	@Override
	public TemplateRulesDTO findByContextIdAndFactTypeAndPagination(ObjectId contextId, RuleFactType type, int page,int size, String filter) {
		
		TemplateRulesDTO dto = new TemplateRulesDTO();
		
		AggregationOperation matchContextId = Aggregation.match(new Criteria("contextId").is(contextId));
		AggregationOperation matchRuleFactType = Aggregation.match(new Criteria("type").is(type));
		AggregationOperation lookUpRules = Aggregation.lookup("templateRule", "ruleId", "_id", "templateRule");
		AggregationOperation unwindRule = Aggregation.unwind("$templateRule");
		AggregationOperation removeMappingId = Aggregation.replaceRoot("$templateRule");
		
		AggregationOperation countTotal = Aggregation.count().as(StaticConfigItems.COUNT_FIELD);
		
		

		
		String[] filterFields = { "name", "description" };
		
		AggregationOperation filtering = Aggregation.match(defineFilterCriteriaWithManyFields(filterFields, filter));

		Aggregation aggregation;
		
		if(!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(TemplateRule.class, matchContextId, matchRuleFactType, lookUpRules, unwindRule, removeMappingId, filtering, countTotal);
		} else {
			aggregation = Aggregation.newAggregation(TemplateRule.class, matchContextId, matchRuleFactType, lookUpRules, unwindRule, removeMappingId, countTotal);
		}
		
		Object countObject = getCountResult(mongoTemplate.aggregate(aggregation, "templateRuleFactTypePair", Object.class));

		
		int limit = portalUtils.getPaginationLimit(size);
		long skip = portalUtils.getPaginationStart(size, page, limit);
		AggregationOperation paginationLimit = Aggregation.limit(limit);
		AggregationOperation paginationSkip = Aggregation.skip(skip);
		
		
		if(!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(TemplateRule.class, matchContextId, matchRuleFactType, lookUpRules, unwindRule, removeMappingId, filtering, paginationSkip, paginationLimit);
		} else {
			aggregation = Aggregation.newAggregation(TemplateRule.class, matchContextId, matchRuleFactType, lookUpRules, unwindRule, removeMappingId, paginationSkip, paginationLimit);
		}
		
		
		AggregationResults<TemplateRule> result = mongoTemplate.aggregate(aggregation, "templateRuleFactTypePair", TemplateRule.class);

		dto.setTemplateRules(result.getMappedResults());
		
		long count = Long.valueOf(countObject.toString());
		dto.setTotalSize(count);
		
		return dto;
	}
}
