package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TemplateRuleRepository extends MongoRepository<TemplateRule>{
	
	public abstract List<TemplateRule> findByContextId(String contextId);
}
