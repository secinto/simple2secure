package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.OsQuery;
import com.simple2secure.portal.repository.OsQueryRepository;

@Repository
@Transactional
public class OsQueryRepositoryImpl extends OsQueryRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "osquery"; //$NON-NLS-1$
		super.className = OsQuery.class;
	}

	@Override
	public List<OsQuery> findByActiveStatus(int active) {
		Query query = new Query(Criteria.where("active").is(active));
		return mongoTemplate.find(query, OsQuery.class, collectionName);
	}

	@Override
	public List<OsQuery> findByCategoryId(String categoryId) {
		Query query = new Query(Criteria.where("categoryId").is(categoryId));
		return mongoTemplate.find(query, OsQuery.class, collectionName);
	}

}
