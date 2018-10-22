package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.Config;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class ConfigRepository extends MongoRepository<Config> {	
	public abstract Config findByProbeId(String probeId);

	public abstract void deleteByProbeId(String probeId);
	
	public abstract Config findByGroupId(String groupId);
	
	public abstract List<Config> findAllByGroupId(String groupId);
	
	public abstract void deleteByGroupId(String groupId);
}
