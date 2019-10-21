package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.OSInfo;
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

		return mongoTemplate.find(query, QueryRun.class, collectionName);
	}

	@Override
	public List<QueryRun> findByGroupIdAndOSInfo(String groupId, OSInfo osInfo, boolean selectAll) {
		Query query = new Query();

		if (selectAll) {
			List<Integer> possibleValues = new ArrayList<>();
			switch (osInfo) {
			case WINDOWS:
				possibleValues.add(1);
				possibleValues.add(3);
				possibleValues.add(5);
				possibleValues.add(7);
				break;
			case LINUX:
				possibleValues.add(2);
				possibleValues.add(3);
				possibleValues.add(6);
				possibleValues.add(7);
				break;
			case OSX:
				possibleValues.add(4);
				possibleValues.add(5);
				possibleValues.add(6);
				possibleValues.add(7);
				break;
			default:
				possibleValues.add(1);
				possibleValues.add(2);
				possibleValues.add(3);
				possibleValues.add(4);
				possibleValues.add(5);
				possibleValues.add(6);
				possibleValues.add(7);
				break;
			}
			query = new Query(Criteria.where("groupId").is(groupId).and("systemsAvailable").in(possibleValues));

		} else {
			query = new Query(Criteria.where("groupId").is(groupId).and("active").is(1));
		}

		return mongoTemplate.find(query, QueryRun.class, collectionName);
	}

	@Override
	public QueryRun findByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		QueryRun result = mongoTemplate.findOne(query, QueryRun.class);
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
