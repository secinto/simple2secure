package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
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
	public Email findByConfigAndMessageId(ObjectId configId, String msgId) {
		Query query = new Query(Criteria.where("configId").is(configId).and("messageId").is(msgId));
		return mongoTemplate.findOne(query, Email.class);
	}

	@Override
	public List<Email> findByConfigId(ObjectId configId) {
		Query query = new Query(Criteria.where("configId").is(configId));
		return mongoTemplate.find(query, Email.class);
	}

	@Override
	public void deleteByConfigId(ObjectId configId) {
		List<Email> emails = findByConfigId(configId);

		if (emails != null) {
			for (Email email : emails) {
				this.delete(email);
			}
		}

	}
}
