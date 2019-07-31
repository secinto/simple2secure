package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.EmailList;
import com.simple2secure.api.model.EmailListEnum;
import com.simple2secure.portal.repository.EmailListRepository;

@Repository
@Transactional
public class EmailListRepositoryImpl extends EmailListRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "emailList";
		super.className = EmailList.class;
	}

	@Override
	public List<EmailList> findByEmailType(EmailListEnum type) {
		Query query = new Query(Criteria.where("type").is(type));
		return mongoTemplate.find(query, EmailList.class);
	}

}
