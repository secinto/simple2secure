package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.EmailAttribute;
import com.simple2secure.api.model.EmailAttributeEnum;
import com.simple2secure.portal.repository.EmailAttributesListRepository;

@Component
@Repository
@Transactional
public class EmailAttributesListRepositoryImpl extends EmailAttributesListRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "emailAttribute";
		super.className = EmailAttribute.class;
	}

	@Override
	public List<EmailAttribute> findByAttributeType(EmailAttributeEnum type) {
		Query query = new Query(Criteria.where("type").is(type));
		return mongoTemplate.find(query, EmailAttribute.class);
	}

}
