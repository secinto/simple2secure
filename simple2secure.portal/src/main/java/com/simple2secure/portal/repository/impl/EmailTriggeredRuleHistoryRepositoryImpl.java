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

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TriggeredRuleEmail;
import com.simple2secure.portal.repository.EmailTriggeredRuleHistoryRepository;

@Repository
@Transactional
public class EmailTriggeredRuleHistoryRepositoryImpl extends EmailTriggeredRuleHistoryRepository{

	@PostConstruct
	public void init() {
		super.collectionName = "triggeredRuleEmail";
		super.className = TriggeredRuleEmail.class;
	}
	
	@Override
	public TriggeredRuleEmail findByRuleName(String ruleName) {
		// TODO Auto-generated method stub
		
		Query query = new Query(Criteria.where("rule.name").is(ruleName));
		return mongoTemplate.findOne(query, TriggeredRuleEmail.class, collectionName);
	}

	@Override
	public void deleteByRuleId(String ruleName) {
		// TODO Auto-generated method stub
		//TODO: implement
		
	}
}
