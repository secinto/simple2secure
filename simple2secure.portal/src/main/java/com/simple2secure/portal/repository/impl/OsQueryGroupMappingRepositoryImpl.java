package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.OsQueryGroupMapping;
import com.simple2secure.portal.repository.OsQueryGroupMappingRepository;

@Repository
@Transactional
public class OsQueryGroupMappingRepositoryImpl extends OsQueryGroupMappingRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "osQueryGroupMapping"; //$NON-NLS-1$
		super.className = OsQueryGroupMapping.class;
	}

	@Override
	public List<OsQueryGroupMapping> findByGroupId(ObjectId groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		return mongoTemplate.find(query, OsQueryGroupMapping.class, collectionName);
	}

	@Override
	public void deleteByGroupId(ObjectId groupId) {
		List<OsQueryGroupMapping> mappings = findByGroupId(groupId);

		if (mappings != null) {
			for (OsQueryGroupMapping mapping : mappings) {
				this.delete(mapping);
			}
		}
	}

	@Override
	public List<OsQueryGroupMapping> findByQueryId(ObjectId queryId) {
		Query query = new Query(Criteria.where("queryId").is(queryId));
		return mongoTemplate.find(query, OsQueryGroupMapping.class, collectionName);
	}

	@Override
	public void deleteByQueryId(ObjectId queryId) {
		List<OsQueryGroupMapping> mappings = findByQueryId(queryId);

		if (mappings != null) {
			for (OsQueryGroupMapping mapping : mappings) {
				this.delete(mapping);
			}
		}
	}

	@Override
	public List<OsQueryGroupMapping> findByGroupIdAndOSInfo(ObjectId groupId, List<Integer> possibleValues) {
		Query query = new Query();

		query = new Query(Criteria.where("groupId").is(groupId).and("systemsAvailable").in(possibleValues));

		return mongoTemplate.find(query, OsQueryGroupMapping.class, collectionName);
	}

	@Override
	public List<OsQueryGroupMapping> getAllMapingsByGroupIds(List<ObjectId> group_ids, List<Integer> possibleValues) {
		List<OsQueryGroupMapping> mappings = new ArrayList<>();
		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (ObjectId groupId : group_ids) {
			Criteria expression = new Criteria();
			expression.and("groupId").is(groupId);
			orExpression.add(expression);
		}
		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		query.addCriteria(Criteria.where("systemsAvailable").in(possibleValues));

		mappings = mongoTemplate.find(query, OsQueryGroupMapping.class, collectionName);

		return mappings;
	}
}
