package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.ServiceLibrary;
import com.simple2secure.portal.repository.ServiceLibraryRepository;

@Repository
@Transactional
public class ServiceLibraryRepositoryImpl extends ServiceLibraryRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "serviceLibrary";
		super.className = ServiceLibrary.class;
	}

	@Override
	public ServiceLibrary findByVersion(String version) {
		Query query = new Query(Criteria.where("version").is(new ObjectId(version)));
		return mongoTemplate.findOne(query, ServiceLibrary.class);
	}
}
