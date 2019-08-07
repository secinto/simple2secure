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
		return this.mongoTemplate.find(query, PortalRule.class, this.collectionName);
	}

	@Override
	public List<PortalRule> findByUserID(String userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		return this.mongoTemplate.find(query, PortalRule.class, this.collectionName);
	}

	@Override
	public List<PortalRule> findByToolAndUserId(String toolId, String userId) {
		Query query = new Query(Criteria.where("userId").is(userId).and("toolId").is(toolId));
		return this.mongoTemplate.find(query, PortalRule.class, this.collectionName);
	}

	@Override
	public void deleteByUserId(String userId) {
		List<PortalRule> rules = findByUserID(userId);
		
		if(rules != null) {
			for(PortalRule rule : rules) {
				this.delete(rule);
			}
		}
		
	}	
}
