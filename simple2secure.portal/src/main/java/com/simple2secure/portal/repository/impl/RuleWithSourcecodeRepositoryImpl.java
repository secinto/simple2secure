package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.RuleWithSourcecode;
import com.simple2secure.portal.repository.RuleWithSourcecodeRepository;

@Repository
@Transactional
public class RuleWithSourcecodeRepositoryImpl extends RuleWithSourcecodeRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "ruleWithSourcecode";
		super.className = RuleWithSourcecode.class;
	}

	@Override
	public List<RuleWithSourcecode> findByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextID").is(contextId));
		return mongoTemplate.find(query, RuleWithSourcecode.class);
	}
}
