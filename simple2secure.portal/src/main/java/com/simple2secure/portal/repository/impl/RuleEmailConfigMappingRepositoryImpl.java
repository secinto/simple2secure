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

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.RuleEmailConfigMapping;
import com.simple2secure.portal.repository.RuleEmailConfigMappingRepository;

@Repository
@Transactional
public class RuleEmailConfigMappingRepositoryImpl extends RuleEmailConfigMappingRepository{

	@PostConstruct
	public void init() {
		super.collectionName = "ruleEmailConfigMapping";
		super.className = RuleEmailConfigMapping.class;
	}
	
	@Override
	public List<RuleEmailConfigMapping> getByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<RuleEmailConfigMapping> ruleUserPairs = mongoTemplate.find(query, RuleEmailConfigMapping.class);
		return ruleUserPairs;
	}

	@Override
	public List<RuleEmailConfigMapping> getByContextIdAndEmailConfigId(ObjectId contextId, ObjectId emailConfigurationId) {
		Query query = new Query(Criteria.where("emailConfigurationId").is(emailConfigurationId)
				.and("contextId").is(contextId));
		List<RuleEmailConfigMapping> ruleUserPairs = mongoTemplate.find(query, RuleEmailConfigMapping.class);
		return ruleUserPairs;
	}

	@Override
	public List<RuleEmailConfigMapping> getByContextIdAndRuleId(ObjectId contextId, ObjectId ruleId) {
		Query query = new Query(Criteria.where("ruleId").is(ruleId).and("contextId").is(contextId));
		List<RuleEmailConfigMapping> ruleUserPairs = mongoTemplate.find(query, RuleEmailConfigMapping.class);
		return ruleUserPairs;
	}
	
	@Override
	public RuleEmailConfigMapping getByEmailConfigIdAndRuleId(ObjectId contextId, ObjectId emailConfigurationId,
			ObjectId ruleId) {
		Query query = new Query(Criteria.where("emailConfigurationId").is(emailConfigurationId)
				.and("ruleId").is(ruleId).and("contextId").is(contextId));
		RuleEmailConfigMapping ruleUserPair = mongoTemplate.findOne(query, RuleEmailConfigMapping.class);
		return ruleUserPair;
	}

	@Override
	public void deleteByContextId(ObjectId contextId) {
		List<RuleEmailConfigMapping> ruleUserPairs = getByContextId(contextId);

		if (ruleUserPairs != null) {
			for (RuleEmailConfigMapping ruleUserPair : ruleUserPairs) {
				mongoTemplate.remove(ruleUserPair);
			}
		}		
	}

	@Override
	public void deleteByEmailConfig(ObjectId contextId, ObjectId emailConfigurationId) {
		List<RuleEmailConfigMapping> ruleUserPairs = getByContextIdAndEmailConfigId(contextId, emailConfigurationId);

		if (ruleUserPairs != null) {
			for (RuleEmailConfigMapping ruleUserPair : ruleUserPairs) {
				mongoTemplate.remove(ruleUserPair);
			}
		}	
	}

	@Override
	public void deleteByRuleId(ObjectId contextId, ObjectId ruleId) {
		List<RuleEmailConfigMapping> ruleUserPairs = getByContextIdAndRuleId(contextId, ruleId);

		if (ruleUserPairs != null) {
			for (RuleEmailConfigMapping ruleUserPair : ruleUserPairs) {
				mongoTemplate.remove(ruleUserPair);
			}
		}			
	}

	@Override
	public void deleteByEmailConfigIdAndRuleId(ObjectId contextId, ObjectId emailConfigurationId, ObjectId ruleId) {
		RuleEmailConfigMapping ruleUserPair = getByEmailConfigIdAndRuleId(contextId, emailConfigurationId, ruleId);
		
		if(ruleUserPair != null)
			mongoTemplate.remove(ruleUserPair);
	}
}
