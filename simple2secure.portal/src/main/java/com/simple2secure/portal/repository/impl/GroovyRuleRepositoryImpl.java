package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.GroovyRule;
import com.simple2secure.portal.repository.GroovyRuleRepository;

@Repository
@Transactional
public class GroovyRuleRepositoryImpl extends GroovyRuleRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "groovyRule";
		super.className = GroovyRule.class;
	}

	@Override
	public List<GroovyRule> findByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextID").is(contextId));
		return mongoTemplate.find(query, GroovyRule.class);
	}
}
