package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.RuleWithSourcecode;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.portal.repository.TemplateRuleRepository;

@Repository
@Transactional
public class TemplateRuleRepositoryImpl extends TemplateRuleRepository{

	@PostConstruct
	public void init() {
		super.collectionName = "templateRule";
		super.className = TemplateRule.class;
	}
	
	@Override
	public List<TemplateRule> findByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextID").is(contextId));
		return mongoTemplate.find(query, TemplateRule.class);
	}

}
