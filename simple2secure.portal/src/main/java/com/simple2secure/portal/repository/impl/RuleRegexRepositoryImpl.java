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

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.dto.RuleRegexDTO;
import com.simple2secure.api.model.RuleRegex;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.RuleRegexRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class RuleRegexRepositoryImpl extends RuleRegexRepository{
	
	@Autowired
	PortalUtils portalUtils;
	
	@PostConstruct
	public void init() {
		super.collectionName = "ruleRegex";
		super.className = RuleRegex.class;
	}

	@Override
	public List<RuleRegex> findByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		return mongoTemplate.find(query, RuleRegex.class);
	}

	@Override
	public RuleRegex findByContextIdAndName(ObjectId contextId, String name) {
		Query query = new Query(Criteria.where("name").is(name).and("contextId").is(contextId));
		return mongoTemplate.findOne(query, RuleRegex.class);
	}

	@Override
	public RuleRegex findByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		return mongoTemplate.findOne(query, RuleRegex.class);
	}

	@Override
	public RuleRegexDTO findByContextIdAndPagination(ObjectId contextId, int page, int size, String filter) {
		
		RuleRegexDTO dto = new RuleRegexDTO();
		
		String[] filterFields = { "name", "description" };

		AggregationOperation matchContextId = Aggregation.match(new Criteria("contextId").is(contextId));
		AggregationOperation filtering = Aggregation.match(defineFilterCriteriaWithManyFields(filterFields, filter));
		AggregationOperation countTotal = Aggregation.count().as(StaticConfigItems.COUNT_FIELD);
		
		Aggregation aggregation;
		
		if(!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(RuleRegex.class, matchContextId, filtering, countTotal);
		} else {
			aggregation = Aggregation.newAggregation(RuleRegex.class, matchContextId, countTotal);
		}
		
		Object countObject = getCountResult(mongoTemplate.aggregate(aggregation, "ruleRegex", Object.class));
		
		int limit = portalUtils.getPaginationLimit(size);
		long skip = portalUtils.getPaginationStart(size, page, limit);
		AggregationOperation paginationLimit = Aggregation.limit(limit);
		AggregationOperation paginationSkip = Aggregation.skip(skip);
		
		if(!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(RuleRegex.class, matchContextId, filtering, paginationSkip, paginationLimit);
		} else {
			aggregation = Aggregation.newAggregation(RuleRegex.class, matchContextId, paginationSkip, paginationLimit);
		}
		
		AggregationResults<RuleRegex> result = mongoTemplate.aggregate(aggregation, "ruleRegex", RuleRegex.class);
		
		long count = Long.valueOf(countObject.toString());
		dto.setRegexes(result.getMappedResults());
		dto.setTotalSize((long) count);
		
		return dto;
	}

}
