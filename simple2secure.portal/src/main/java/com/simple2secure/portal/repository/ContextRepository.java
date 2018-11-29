package com.simple2secure.portal.repository;

import com.simple2secure.api.model.Context;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class ContextRepository extends MongoRepository<Context> {	
	public abstract Context getContextByUserId(String userId);
	public abstract Context deleteByContextId(String contextId);
}
