package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.RuleWithSourcecode;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class RuleWithSourcecodeRepository extends MongoRepository<RuleWithSourcecode> {
	public abstract List<RuleWithSourcecode> findByContextId(String contextId);
}
