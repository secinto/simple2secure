package com.simple2secure.portal.repository;
import java.util.List;

import com.simple2secure.api.model.Email;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class EmailRepository extends MongoRepository<Email> {
	public abstract Email findByUserUUIDConfigIDAndMsgID(String userUUID, String configID, String msgId);
	public abstract List<Email> findByUserUUIDAndConfigID(String userUUID, String configID);
	public abstract List<Email> findByUserId(String userId);
	public abstract void deleteByUserId(String userId);
}
