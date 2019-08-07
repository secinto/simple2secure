package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.PortalRule;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class RuleRepository extends MongoRepository<PortalRule> {
	public abstract List<PortalRule> findByToolId(String toolId);
	public abstract List<PortalRule> findByUserID(String userId);
	public abstract List<PortalRule> findByToolAndUserId(String toolId, String userId);
	public abstract void deleteByUserId(String userId);
}
