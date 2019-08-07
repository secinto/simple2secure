package com.simple2secure.portal.repository;
import java.util.List;

import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class EmailConfigurationRepository extends MongoRepository<EmailConfiguration> {
	public abstract List<EmailConfiguration> findByUserUUID(String userUUID);
	public abstract EmailConfiguration findByEmailConfigId(String emailConfigID);
	public abstract EmailConfiguration findByConfigId(String emailConfigID);
	public abstract EmailConfiguration deleteByConfigId(String emailConfigID);
	public abstract void deleteByUserId(String userId);
}
