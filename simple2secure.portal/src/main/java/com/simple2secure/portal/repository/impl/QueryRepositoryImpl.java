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
	public List<QueryRun> findByProbeId(String probeId, boolean selectAll) {
		Query query = new Query();
		if (selectAll) {
			query = new Query(Criteria.where("probeId").is(probeId));
		} else {
			query = new Query(Criteria.where("probeId").is(probeId).and("active").is(1));
		}

		return this.mongoTemplate.find(query, QueryRun.class, this.collectionName);
	}
	
	@Override
	public List<QueryRun> findByGroupId(String groupId, boolean selectAll, boolean isGroupQueryRun) {
		Query query = new Query();
		
		if (selectAll) {
			if(isGroupQueryRun) {
				query = new Query(Criteria.where("groupId").is(groupId).and("isGroupQueryRun").is(isGroupQueryRun));
			}
			else {
				query = new Query(Criteria.where("groupId").is(groupId));
			}
			
		} else {
			if(isGroupQueryRun) {
				query = new Query(Criteria.where("groupId").is(groupId).and("active").is(1).and("isGroupQueryRun").is(isGroupQueryRun));
			}
			else {
				query = new Query(Criteria.where("groupId").is(groupId).and("active").is(1));
			}
			
		}
		
		return this.mongoTemplate.find(query, QueryRun.class, this.collectionName); 
	}	

	@Override
	public void deleteByProbeId(String probeId) {
		List<QueryRun> queries = findByProbeId(probeId, true);
		for (QueryRun query : queries) {
			this.mongoTemplate.remove(query);
		}
		
	}

	@Override
	public QueryRun findByNameAndProbeId(String probeId, String name) {
		List<QueryRun> queries = findByProbeId(probeId, true);

		if (queries == null) {
			return null;
		} else {
			for (QueryRun query : queries) {
				if (query.getName().equals(name)) {
					return query;
				}
			}
			return null;
		}
	}

	@Override
	public QueryRun findByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		QueryRun result = this.mongoTemplate.findOne(query, QueryRun.class);
		return result;
	}

	@Override
	public void deleteByGroupId(String groupId) {
		List<QueryRun> queries = findByGroupId(groupId, true, false);
		
		if(queries != null) {
			for(QueryRun query : queries) {
				this.delete(query);
			}
		}
		
	}
}
