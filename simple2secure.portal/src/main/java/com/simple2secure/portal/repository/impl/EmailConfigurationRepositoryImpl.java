package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.RuleEmailConfigMappingRepository;

@Repository
@Transactional
public class EmailConfigurationRepositoryImpl extends EmailConfigurationRepository {

	@Autowired
	private RuleEmailConfigMappingRepository ruleEmailConfigMappingRepository;
	
	@PostConstruct
	public void init() {
		super.collectionName = "emailConfiguration";
		super.className = EmailConfiguration.class;
	}

	@Override
	public List<EmailConfiguration> findByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		return mongoTemplate.find(query, EmailConfiguration.class);
	}

	@Override
	public void deleteByContextId(ObjectId contextId) {
		List<EmailConfiguration> configs = findByContextId(contextId);
		if (configs != null) {
			for (EmailConfiguration config : configs) {
				ruleEmailConfigMappingRepository.deleteByEmailConfig(contextId, config.getId());
				this.delete(config);
			}
		}
	}

	@Override
	public EmailConfiguration findByEmailAndContextId(String name, ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId).and("name").is(name));
		return mongoTemplate.findOne(query, EmailConfiguration.class);

	}
}
