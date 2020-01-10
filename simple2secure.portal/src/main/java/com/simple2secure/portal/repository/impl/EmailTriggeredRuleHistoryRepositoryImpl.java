package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TriggeredRule;
import com.simple2secure.portal.repository.EmailRuleTriggeredRepository;

@Repository
@Transactional
public class EmailRuleTriggeredRepositoryImpl extends EmailRuleTriggeredRepository{

	@PostConstruct
	public void init() {
		super.collectionName = "triggeredRule";
		super.className = TriggeredRule.class;
	}
	
	@Override
	public TriggeredRule findByRuleName(String ruleName) {
		// TODO Auto-generated method stub
		
		Query query = new Query(Criteria.where("rule.name").is(ruleName));
		return mongoTemplate.findOne(query, TriggeredRule.class, collectionName);
	}

	@Override
	public void deleteByRuleId(String ruleName) {
		// TODO Auto-generated method stub
		//TODO: implement
		
	}
}
