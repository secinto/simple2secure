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
import com.simple2secure.api.model.TemplateActionFactTypePair;
import com.simple2secure.portal.repository.TemplateActionFactTypeMappingRepository;

@Component
@Repository
@Transactional
public class TemplateActionFactTypeMappingRepositoryImpl extends TemplateActionFactTypeMappingRepository
{
	
	@PostConstruct
	public void init() {
		super.collectionName = "templateActionFactTypePair";
		super.className = TemplateActionFactTypePair.class;
	}

	@Override
	public List<ObjectId> findActionsIdsByFactType(RuleFactType type) {
		Query query = new Query(Criteria.where("type").is(type));
		List<TemplateActionFactTypePair> pairs = mongoTemplate.find(query, TemplateActionFactTypePair.class);
		
		List<ObjectId>  actionIds = new ArrayList<ObjectId>();
		if(pairs != null && pairs.size() > 0)
		{
			pairs.forEach(pair ->{
				actionIds.add(pair.getTemplateActionId());
			});
		}
		
		return actionIds;
	}
	
	

}
