package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.GroovyRule;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class GroovyRuleRepository extends MongoRepository<GroovyRule> {
	public abstract List<GroovyRule> findByContextId(String contextId);
}
