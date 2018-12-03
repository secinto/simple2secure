package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class ContextUserAuthRepository extends MongoRepository<ContextUserAuthentication> {

	public abstract List<ContextUserAuthentication> getByContextId(String contextId);

	public abstract List<ContextUserAuthentication> getByUserId(String userId);

	public abstract ContextUserAuthentication getByContextIdAndUserId(String contextId, String userId);

	public abstract void deleteByContextId(String contextId);

	public abstract void deleteByUserId(String userId);

	public abstract void deleteByContextIdAndUserId(String contextId, String userId);

	public abstract void deleteById(String id);
}
