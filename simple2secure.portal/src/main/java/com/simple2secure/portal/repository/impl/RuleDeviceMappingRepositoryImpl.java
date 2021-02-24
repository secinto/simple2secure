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

import com.simple2secure.api.model.RuleDeviceMapping;
import com.simple2secure.portal.repository.RuleDeviceMappingRepository;

@Repository
@Transactional
public class RuleDeviceMappingRepositoryImpl extends RuleDeviceMappingRepository
{
	@PostConstruct
	public void init() {
		super.collectionName = "ruleDeviceMapping";
		super.className = RuleDeviceMapping.class;
	}

	@Override
	public List<RuleDeviceMapping> getByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<RuleDeviceMapping> ruleDeviceMappings = mongoTemplate.find(query, RuleDeviceMapping.class);
		return ruleDeviceMappings;
	}
	
	@Override
	public List<RuleDeviceMapping> getByContextIdAndRuleId(ObjectId contextId, ObjectId ruleId) {
		Query query = new Query(Criteria.where("ruleId").is(ruleId).and("contextId").is(contextId));
		List<RuleDeviceMapping> ruleDeviceMappings = mongoTemplate.find(query, RuleDeviceMapping.class);
		return ruleDeviceMappings;
	}
	
	@Override
	public List<RuleDeviceMapping> getByDeviceId(ObjectId deviceId){
		Query query = new Query(Criteria.where("deviceId").is(deviceId));
		List<RuleDeviceMapping> ruleDeviceMappings = mongoTemplate.find(query, RuleDeviceMapping.class);
		return ruleDeviceMappings;
	}

	@Override
	public void deleteByContextId(ObjectId contextId) {
		List<RuleDeviceMapping> ruleDeviceMappings = getByContextId(contextId);

		if (ruleDeviceMappings != null) {
			for (RuleDeviceMapping ruleDeviceMapping : ruleDeviceMappings) {
				mongoTemplate.remove(ruleDeviceMapping);
			}
		}
	}

	@Override
	public void deleteByDeviceId(ObjectId deviceId) {
		List<RuleDeviceMapping> ruleDeviceMappings = getByDeviceId(deviceId);
		
		if(ruleDeviceMappings != null)
		{
			ruleDeviceMappings.forEach(mongoTemplate::remove);
		}
	}

	@Override
	public void deleteByRuleId(ObjectId contextId, ObjectId ruleId) {
		List<RuleDeviceMapping> ruleDeviceMappings = getByContextIdAndRuleId(contextId, ruleId);
		
		if(ruleDeviceMappings != null)
		{
			ruleDeviceMappings.forEach(mongoTemplate::remove);
		}
	}

	@Override
	public List<RuleDeviceMapping> getByDeviceIdAndContextId(ObjectId deviceId, ObjectId contextId) {
		Query query = new Query(Criteria.where("deviceId").is(deviceId).and("contextId").is(contextId));
		List<RuleDeviceMapping> ruleDeviceMappings = mongoTemplate.find(query, RuleDeviceMapping.class);
		return ruleDeviceMappings;
	}

}
