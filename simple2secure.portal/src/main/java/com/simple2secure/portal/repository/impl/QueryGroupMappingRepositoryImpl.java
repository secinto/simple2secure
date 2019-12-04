package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.QueryGroupMapping;
import com.simple2secure.portal.repository.QueryGroupMappingRepository;

@Repository
@Transactional
public class QueryGroupMappingRepositoryImpl extends QueryGroupMappingRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "querygroupmapping"; //$NON-NLS-1$
		super.className = QueryGroupMapping.class;
	}

	@Override
	public List<QueryGroupMapping> findByGroupId(String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		return mongoTemplate.find(query, QueryGroupMapping.class, collectionName);
	}
}
