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

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.RuleUserPair;
import com.simple2secure.portal.repository.RuleUserPairsRepository;

@Repository
@Transactional
public class RuleUserPairsRepositoryImpl extends RuleUserPairsRepository{

	@PostConstruct
	public void init() {
		super.collectionName = "ruleUserPair";
		super.className = RuleUserPair.class;
	}
	
	@Override
	public List<RuleUserPair> getByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<RuleUserPair> ruleUserPairs = mongoTemplate.find(query, RuleUserPair.class);
		return ruleUserPairs;
	}

	@Override
	public List<RuleUserPair> getByContextIdAndEmailConfigId(String contextId, String emailConfigurationId) {
		Query query = new Query(Criteria.where("emailConfigurationId").is(emailConfigurationId)
				.and("contextId").is(contextId));
		List<RuleUserPair> ruleUserPairs = mongoTemplate.find(query, RuleUserPair.class);
		return ruleUserPairs;
	}

	@Override
	public List<RuleUserPair> getByContextIdAndRuleId(String contextId, String ruleId) {
		Query query = new Query(Criteria.where("ruleId").is(ruleId).and("contextId").is(contextId));
		List<RuleUserPair> ruleUserPairs = mongoTemplate.find(query, RuleUserPair.class);
		return ruleUserPairs;
	}
	
	@Override
	public RuleUserPair getByEmailConfigIdAndRuleId(String contextId, String emailConfigurationId,
			String ruleId) {
		Query query = new Query(Criteria.where("emailConfigurationId").is(emailConfigurationId)
				.and("ruleId").is(ruleId).and("contextId").is(contextId));
		RuleUserPair ruleUserPair = mongoTemplate.findOne(query, RuleUserPair.class);
		return ruleUserPair;
	}

	@Override
	public void deleteByContextId(String contextId) {
		List<RuleUserPair> ruleUserPairs = getByContextId(contextId);

		if (ruleUserPairs != null) {
			for (RuleUserPair ruleUserPair : ruleUserPairs) {
				mongoTemplate.remove(ruleUserPair);
			}
		}		
	}

	@Override
	public void deleteByEmailConfig(String contextId, String emailConfigurationId) {
		List<RuleUserPair> ruleUserPairs = getByContextIdAndEmailConfigId(contextId, emailConfigurationId);

		if (ruleUserPairs != null) {
			for (RuleUserPair ruleUserPair : ruleUserPairs) {
				mongoTemplate.remove(ruleUserPair);
			}
		}	
	}

	@Override
	public void deleteByRuleId(String contextId, String ruleId) {
		List<RuleUserPair> ruleUserPairs = getByContextIdAndRuleId(contextId, ruleId);

		if (ruleUserPairs != null) {
			for (RuleUserPair ruleUserPair : ruleUserPairs) {
				mongoTemplate.remove(ruleUserPair);
			}
		}			
	}

	@Override
	public void deleteByEmailConfigIdAndRuleId(String contextId, String emailConfigurationId, String ruleId) {
		RuleUserPair ruleUserPair = getByEmailConfigIdAndRuleId(contextId, emailConfigurationId, ruleId);
		
		if(ruleUserPair != null)
			mongoTemplate.remove(ruleUserPair);
	}
}
