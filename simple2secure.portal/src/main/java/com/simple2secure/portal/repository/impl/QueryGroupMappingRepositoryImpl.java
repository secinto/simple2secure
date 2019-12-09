package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
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

	@Override
	public void deleteByGroupId(String groupId) {
		List<QueryGroupMapping> mappings = findByGroupId(groupId);

		if (mappings != null) {
			for (QueryGroupMapping mapping : mappings) {
				this.delete(mapping);
			}
		}
	}

	@Override
	public List<QueryGroupMapping> findByQueryId(String queryId) {
		Query query = new Query(Criteria.where("queryId").is(queryId));
		return mongoTemplate.find(query, QueryGroupMapping.class, collectionName);
	}

	@Override
	public void deleteByQueryId(String queryId) {
		List<QueryGroupMapping> mappings = findByQueryId(queryId);

		if (mappings != null) {
			for (QueryGroupMapping mapping : mappings) {
				this.delete(mapping);
			}
		}
	}

	@Override
	public List<QueryGroupMapping> findByGroupIdAndOSInfo(String groupId, List<Integer> possibleValues, boolean selectAll) {
		Query query = new Query();

		if (selectAll) {
			query = new Query(Criteria.where("groupId").is(groupId).and("systemsAvailable").in(possibleValues));
		} else {
			query = new Query(Criteria.where("groupId").is(groupId).and("systemsAvailable").in(possibleValues).and("active").is(1));
		}

		return mongoTemplate.find(query, QueryGroupMapping.class, collectionName);
	}

	@Override
	public List<QueryGroupMapping> getAllMapingsByGroupIds(List<String> group_ids, List<Integer> possibleValues, boolean selectAll) {
		List<QueryGroupMapping> mappings = new ArrayList<>();
		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (String groupId : group_ids) {
			Criteria expression = new Criteria();
			expression.and("groupId").is(groupId);
			orExpression.add(expression);
		}
		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

		if (selectAll) {
			query.addCriteria(Criteria.where("systemsAvailable").in(possibleValues));
		} else {
			query.addCriteria(Criteria.where("systemsAvailable").in(possibleValues).and("active").is(1));
		}

		mappings = mongoTemplate.find(query, QueryGroupMapping.class, collectionName);

		return mappings;
	}
}
