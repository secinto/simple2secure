package com.simple2secure.portal.repository;

import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.TriggeredRule;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class EmailRuleTriggeredRepository extends MongoRepository<TriggeredRule> {
	public abstract TriggeredRule findByRuleName(String ruleName);
	
	public abstract void deleteByRuleId(String ruleName);

}
