package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.QueryRun;
import com.simple2secure.portal.repository.QueryRepository;

@Repository
@Transactional
public class QueryRepositoryImpl extends QueryRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "queryrun"; //$NON-NLS-1$
		super.className = QueryRun.class;
	}

	@Override
	public List<QueryRun> findByActiveStatus(int active) {
		Query query = new Query(Criteria.where("active").is(active));
		return mongoTemplate.find(query, QueryRun.class, collectionName);
	}

	@Override
	public List<QueryRun> findByCategoryId(String categoryId) {
		Query query = new Query(Criteria.where("categoryId").is(categoryId));
		return mongoTemplate.find(query, QueryRun.class, collectionName);
	}

}
