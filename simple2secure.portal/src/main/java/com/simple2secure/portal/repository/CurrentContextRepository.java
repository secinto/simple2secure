package com.simple2secure.portal.repository;

import com.simple2secure.api.model.CurrentContext;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class CurrentContextRepository extends MongoRepository<CurrentContext> {
	public abstract CurrentContext findByUserId(String userId);
}
