package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.Rule;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class RuleRepository extends MongoRepository<Rule> {
	public abstract List<Rule> findByContextId(String contextId);
}
