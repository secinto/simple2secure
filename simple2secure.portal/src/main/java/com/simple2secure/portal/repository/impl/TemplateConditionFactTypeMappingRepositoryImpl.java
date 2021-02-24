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

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.TemplateConditionFactTypePair;
import com.simple2secure.portal.repository.TemplateConditionFactTypeMappingRepository;

@Component
@Repository
@Transactional
public class TemplateConditionFactTypeMappingRepositoryImpl extends TemplateConditionFactTypeMappingRepository 
{
	
	@PostConstruct
	public void init() {
		super.collectionName = "templateConditionFactTypePair";
		super.className = TemplateConditionFactTypePair.class;
	}

	@Override
	public List<ObjectId> findConditionIdsFactType(RuleFactType type) {
		Query query = new Query(Criteria.where("type").is(type));
		List<TemplateConditionFactTypePair> pairs = mongoTemplate.find(query, TemplateConditionFactTypePair.class);
		
		List<ObjectId>  conditionIds = new ArrayList<ObjectId>();
		if(pairs != null && pairs.size() > 0)
		{
			pairs.forEach(pair ->{
				conditionIds.add(pair.getTemplateConditionId());
			});
		}
		
		return conditionIds;
	}

}
