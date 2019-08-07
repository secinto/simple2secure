package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class EmailConfigurationRepository extends MongoRepository<EmailConfiguration> {

	public abstract List<EmailConfiguration> findByContextId(String contexId);

	public abstract EmailConfiguration findByEmailAndContextId(String email, String contextId);

	public abstract void deleteByContextId(String contextId);
}
