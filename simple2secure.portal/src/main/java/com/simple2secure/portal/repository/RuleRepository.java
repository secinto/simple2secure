package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.PortalRule;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class RuleRepository extends MongoRepository<PortalRule> {
	public abstract List<PortalRule> findByToolId(String toolId);

	public abstract List<PortalRule> findByContextId(String contextId);

	public abstract List<PortalRule> findByToolAndContextId(String toolId, String contextId);

	public abstract void deleteByContextId(String contextId);
}
