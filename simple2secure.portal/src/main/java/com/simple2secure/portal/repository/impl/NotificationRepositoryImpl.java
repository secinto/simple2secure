package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.simple2secure.api.model.Notification;
import com.simple2secure.portal.repository.NotificationRepository;

@Repository
@Transactional
public class NotificationRepositoryImpl extends NotificationRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "notification";
		super.className = Notification.class;
	}

	@Override
	public List<Notification> findByUserId(String userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		return this.mongoTemplate.find(query, Notification.class);
	}

	@Override
	public List<Notification> findByToolId(String toolId) {
		Query query = new Query(Criteria.where("toolId").is(toolId));
		return this.mongoTemplate.find(query, Notification.class);
	}

	@Override
	public List<Notification> findByUserAndToolId(String userId, String toolId) {
		Query query = new Query(Criteria.where("toolId").is(toolId).and("userId").is(userId));
		return this.mongoTemplate.find(query, Notification.class);
	}

	@Override
	public void deleteByUserId(String userId) {
		List<Notification> notifications = findByUserId(userId);
		
		if(notifications != null) {
			for(Notification notification : notifications) {
				delete(notification);
			}
		}
		
	}	
}
