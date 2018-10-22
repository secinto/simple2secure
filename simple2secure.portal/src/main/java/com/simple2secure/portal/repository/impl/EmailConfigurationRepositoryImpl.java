package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
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
	public List<EmailConfiguration> findByUserUUID(String userUUID) {
		Query query = new Query(Criteria.where("userUUID").is(userUUID));
		return this.mongoTemplate.find(query, EmailConfiguration.class);
	}

	@Override
	public EmailConfiguration findByEmailConfigId(String emailConfigID) {
		Query query = new Query(Criteria.where("_id").is(new ObjectId(emailConfigID)));
		return this.mongoTemplate.findOne(query, EmailConfiguration.class);		
	}

	@Override
	public EmailConfiguration findByConfigId(String emailConfigID) {
		Query query = new Query(Criteria.where("_id").is(new ObjectId(emailConfigID)));
		return this.mongoTemplate.findOne(query, EmailConfiguration.class);	
	}

	@Override
	public EmailConfiguration deleteByConfigId(String emailConfigID) {
		EmailConfiguration config = findByConfigId(emailConfigID);
		if(config != null) {
			this.mongoTemplate.remove(config);
			return config;
		}
		return null;
	}

	@Override
	public void deleteByUserId(String userId) {
		List<EmailConfiguration> configs = findByUserUUID(userId);
		if(configs != null) {
			for(EmailConfiguration config : configs) {
				this.mongoTemplate.remove(config);
			}
		}			
	}
}
