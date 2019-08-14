/**
*********************************************************************
*   simple2secure is a cyber risk and information security platform.
*   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
*********************************************************************
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU Affero General Public License as
*   published by the Free Software Foundation, either version 3 of the
*   License, or (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*   GNU Affero General Public License for more details.
*
*   You should have received a copy of the GNU Affero General Public License
*   along with this program.  If not, see <https://www.gnu.org/licenses/>.
*
 *********************************************************************
*/

package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Sort;
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
	public List<Notification> findByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		return mongoTemplate.find(query, Notification.class);
	}

	@Override
	public List<Notification> findByToolId(String toolId) {
		Query query = new Query(Criteria.where("toolId").is(toolId));
		return mongoTemplate.find(query, Notification.class);
	}

	@Override
	public List<Notification> findByContextAndToolId(String contextId, String toolId) {
		Query query = new Query(Criteria.where("toolId").is(toolId).and("contextId").is(contextId));
		return mongoTemplate.find(query, Notification.class);
	}

	@Override
	public void deleteByContextId(String contextId) {
		List<Notification> notifications = findByContextId(contextId);

		if (notifications != null) {
			for (Notification notification : notifications) {
				delete(notification);
			}
		}

	}

	@Override
	public List<Notification> findAllSortDescending(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		query = query.with(new Sort(Sort.Direction.DESC, "_id"));

		return mongoTemplate.find(query, Notification.class);
	}
}
