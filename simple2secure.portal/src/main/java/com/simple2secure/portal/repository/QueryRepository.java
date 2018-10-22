package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.QueryRun;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class QueryRepository extends MongoRepository<QueryRun> {
	public abstract List<QueryRun> findByProbeId(String probeId, boolean selectAll);

	public abstract List<QueryRun> findByGroupId(String groupId, boolean selectAll, boolean isGroupQueryRun);
	
	public abstract QueryRun findByName(String name);

	public abstract void deleteByProbeId(String probeId);
	
	public abstract void deleteByGroupId(String groupId);

	public abstract QueryRun findByNameAndProbeId(String probeId, String name);
}
