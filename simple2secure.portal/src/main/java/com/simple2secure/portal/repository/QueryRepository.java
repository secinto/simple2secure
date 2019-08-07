package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.QueryRun;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class QueryRepository extends MongoRepository<QueryRun> {

	public abstract List<QueryRun> findByGroupId(String groupId, boolean selectAll);
	
	public abstract QueryRun findByName(String name);
	
	public abstract void deleteByGroupId(String groupId);

}
