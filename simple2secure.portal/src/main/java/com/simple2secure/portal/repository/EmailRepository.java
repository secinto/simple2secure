package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.Email;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class EmailRepository extends MongoRepository<Email> {
	public abstract Email findByContextMessageAndConfigId(String contextId, String configId, String msgId);

	public abstract List<Email> findByContextAndConfigId(String contextId, String configId);

	public abstract List<Email> findByConfigId(String configId);

	public abstract List<Email> findByContextId(String contextId);

	public abstract void deleteByContextId(String contextId);

	public abstract void deleteByConfigId(String configId);

}
