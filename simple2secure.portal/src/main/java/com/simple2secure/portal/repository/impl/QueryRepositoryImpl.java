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
	public List<QueryRun> findByGroupId(String groupId, boolean selectAll) {
		Query query = new Query();

		if (selectAll) {
			query = new Query(Criteria.where("groupId").is(groupId));

		} else {
			query = new Query(Criteria.where("groupId").is(groupId).and("active").is(1));
		}

		return this.mongoTemplate.find(query, QueryRun.class, this.collectionName);
	}

	@Override
	public QueryRun findByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		QueryRun result = this.mongoTemplate.findOne(query, QueryRun.class);
		return result;
	}

	@Override
	public void deleteByGroupId(String groupId) {
		List<QueryRun> queries = findByGroupId(groupId, true);

		if (queries != null) {
			for (QueryRun query : queries) {
				this.delete(query);
			}
		}
	}
}
