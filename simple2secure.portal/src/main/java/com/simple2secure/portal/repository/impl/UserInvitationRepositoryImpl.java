package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.UserInvitation;
import com.simple2secure.api.model.UserInvitationStatus;
import com.simple2secure.portal.repository.UserInvitationRepository;

@Repository
@Transactional
public class UserInvitationRepositoryImpl extends UserInvitationRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "userInvitation"; //$NON-NLS-1$
		super.className = UserInvitation.class;
	}

	@Override
	public List<UserInvitation> getByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<UserInvitation> userInvitationList = mongoTemplate.find(query, UserInvitation.class);
		return userInvitationList;
	}

	@Override
	public List<UserInvitation> getByUserIdAndStatus(String userId, UserInvitationStatus status) {
		Query query = new Query(Criteria.where("userId").is(userId).and("invitationStatus").is(status));
		List<UserInvitation> userInvitationList = mongoTemplate.find(query, UserInvitation.class);
		return userInvitationList;
	}

	@Override
	public List<UserInvitation> getByUserId(ObjectId userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		List<UserInvitation> userInvitationList = mongoTemplate.find(query, UserInvitation.class);
		return userInvitationList;
	}

	@Override
	public void deleteByContexId(ObjectId contextId) {
		List<UserInvitation> userInvitationList = getByContextId(contextId);
		if (userInvitationList != null) {
			for (UserInvitation userInvitation : userInvitationList) {
				if (userInvitation != null) {
					delete(userInvitation);
				}
			}
		}
	}

	@Override
	public void deleteByUserId(ObjectId userId) {
		List<UserInvitation> userInvitationList = getByUserId(userId);
		if (userInvitationList != null) {
			for (UserInvitation userInvitation : userInvitationList) {
				if (userInvitation != null) {
					delete(userInvitation);
				}
			}
		}
	}

	@Override
	public UserInvitation getByContextIdAndUserId(ObjectId contextId, String userId) {
		Query query = new Query(Criteria.where("userId").is(userId).and("contextId").is(contextId));
		UserInvitation userInvitation = mongoTemplate.findOne(query, UserInvitation.class);
		return userInvitation;
	}
}
