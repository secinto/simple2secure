package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.GroupAccessRight;
import com.simple2secure.portal.repository.GroupAccesRightRepository;

@Repository
@Transactional
public class GroupAccessRightRepositoryImpl extends GroupAccesRightRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "groupAccessRight"; //$NON-NLS-1$
		super.className = GroupAccessRight.class;
	}

	@Override
	public List<GroupAccessRight> findByUserId(ObjectId userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		List<GroupAccessRight> accessRights = mongoTemplate.find(query, GroupAccessRight.class);
		return accessRights;
	}

	@Override
	public List<GroupAccessRight> findByGroupId(ObjectId groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		List<GroupAccessRight> accessRights = mongoTemplate.find(query, GroupAccessRight.class);
		return accessRights;
	}

	@Override
	public List<GroupAccessRight> findByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<GroupAccessRight> accessRights = mongoTemplate.find(query, GroupAccessRight.class);
		return accessRights;
	}

	@Override
	public List<GroupAccessRight> findByContextIdAndUserId(ObjectId contextId, ObjectId userId) {
		Query query = new Query(Criteria.where("contextId").is(contextId).and("userId").is(userId));
		List<GroupAccessRight> accessRights = mongoTemplate.find(query, GroupAccessRight.class);
		return accessRights;
	}

	@Override
	public GroupAccessRight findByGroupIdAndUserId(ObjectId groupId, ObjectId userId) {
		Query query = new Query(Criteria.where("groupId").is(groupId).and("userId").is(userId));
		GroupAccessRight accessRight = mongoTemplate.findOne(query, GroupAccessRight.class);
		return accessRight;
	}

	@Override
	public void deleteByUserId(ObjectId userId) {
		List<GroupAccessRight> accessRights = findByUserId(userId);
		if (accessRights != null) {
			for (GroupAccessRight accessRight : accessRights) {
				delete(accessRight);
			}
		}
	}

	@Override
	public void deleteByGroupId(ObjectId groupId) {
		List<GroupAccessRight> accessRights = findByGroupId(groupId);
		if (accessRights != null) {
			for (GroupAccessRight accessRight : accessRights) {
				delete(accessRight);
			}
		}
	}

	@Override
	public void deleteByContextId(ObjectId contextId) {
		List<GroupAccessRight> accessRights = findByContextId(contextId);
		if (accessRights != null) {
			for (GroupAccessRight accessRight : accessRights) {
				delete(accessRight);
			}
		}
	}

	@Override
	public void deleteByContextIdAndUserId(ObjectId contextId, ObjectId userId) {
		List<GroupAccessRight> accessRights = findByContextIdAndUserId(contextId, userId);
		if (accessRights != null) {
			for (GroupAccessRight accessRight : accessRights) {
				delete(accessRight);
			}
		}
	}
}
