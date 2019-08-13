package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.EmailAttribute;
import com.simple2secure.api.model.Rule;
import com.simple2secure.portal.repository.RuleRepository;

@Repository
@Transactional
public class RuleRepositoryImpl extends RuleRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "rule";
		super.className = Rule.class;
	}

	@Override
	public List<Rule> findByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextID").is(contextId));
		return mongoTemplate.find(query, Rule.class);
	}
}
