package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.PortalRule;
import com.simple2secure.portal.repository.RuleRepository;

@Repository
@Transactional
public class RuleRepositoryImpl extends RuleRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "rule";
		super.className = PortalRule.class;
	}

	@Override
	public List<PortalRule> findByToolId(String toolId) {
		Query query = new Query(Criteria.where("toolId").is(toolId));
		return mongoTemplate.find(query, PortalRule.class, collectionName);
	}

	@Override
	public List<PortalRule> findByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		return mongoTemplate.find(query, PortalRule.class, collectionName);
	}

	@Override
	public List<PortalRule> findByToolAndContextId(String toolId, String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId).and("toolId").is(toolId));
		return mongoTemplate.find(query, PortalRule.class, collectionName);
	}

	@Override
	public void deleteByContextId(String contextId) {
		List<PortalRule> rules = findByContextId(contextId);

		if (rules != null) {
			for (PortalRule rule : rules) {
				this.delete(rule);
			}
		}

	}
}
