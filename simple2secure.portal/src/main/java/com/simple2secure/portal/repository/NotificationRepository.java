package com.simple2secure.portal.repository;
import java.util.List;

import com.simple2secure.api.model.Notification;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class NotificationRepository extends MongoRepository<Notification> {
	public abstract List<Notification> findByUserId(String userId);
	public abstract List<Notification> findByToolId(String toolId);
	public abstract List<Notification> findByUserAndToolId(String userId, String toolId);
	public abstract void deleteByUserId(String userId);
}
