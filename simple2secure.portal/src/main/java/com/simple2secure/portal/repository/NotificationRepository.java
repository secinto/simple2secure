package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.Notification;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class NotificationRepository extends MongoRepository<Notification> {
	public abstract List<Notification> findByContextId(String contextId);

	public abstract List<Notification> findByToolId(String toolId);

	public abstract List<Notification> findByContextAndToolId(String contextId, String toolId);

	public abstract void deleteByContextId(String contextId);

	public abstract List<Notification> findAllSortDescending(String contextId);

}
