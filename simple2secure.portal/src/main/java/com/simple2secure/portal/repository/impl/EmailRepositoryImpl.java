package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Email;
import com.simple2secure.portal.repository.EmailRepository;

@Repository
@Transactional
public class EmailRepositoryImpl extends EmailRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "email";
		super.className = Email.class;
	}

	@Override
	public Email findByUserUUIDConfigIDAndMsgID(String userUUID, String configID, String msgId) {
		Query query = new Query(Criteria.where("userUUID").is(userUUID).and("configID").is(configID).and("messageID").is(msgId));
		return this.mongoTemplate.findOne(query, Email.class);
	}

	@Override
	public List<Email> findByUserUUIDAndConfigID(String userUUID, String configID) {
		Query query = new Query(Criteria.where("userUUID").is(userUUID).and("configID").is(configID));
		return this.mongoTemplate.find(query, Email.class);
	}

	@Override
	public List<Email> findByUserId(String userId) {
		Query query = new Query(Criteria.where("userUUID").is(userId));
		return this.mongoTemplate.find(query, Email.class);		
	}

	@Override
	public void deleteByUserId(String userId) {
		List<Email> emails = findByUserId(userId);
		if(emails != null) {
			for(Email email : emails) {
				this.mongoTemplate.remove(email);
			}
		}		
	}	
}
