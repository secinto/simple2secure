package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.portal.repository.EmailConfigurationRepository;

@Repository
@Transactional
public class EmailConfigurationRepositoryImpl extends EmailConfigurationRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "emailConfiguration";
		super.className = EmailConfiguration.class;
	}

	@Override
	public List<EmailConfiguration> findByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		return mongoTemplate.find(query, EmailConfiguration.class);
	}

	@Override
	public void deleteByContextId(String contextId) {
		List<EmailConfiguration> configs = findByContextId(contextId);
		if (configs != null) {
			for (EmailConfiguration config : configs) {
				mongoTemplate.remove(config);
			}
		}
	}
}
