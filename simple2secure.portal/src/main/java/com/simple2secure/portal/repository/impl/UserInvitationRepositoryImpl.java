package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.UserInvitation;
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
	public UserInvitation getByInvitationToken(String invitationToken) {
		Query query = new Query(Criteria.where("invitationToken").is(invitationToken));
		UserInvitation userInvitation = mongoTemplate.findOne(query, UserInvitation.class);
		return userInvitation;
	}

	@Override
	public List<UserInvitation> getByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<UserInvitation> userInvitationList = mongoTemplate.find(query, UserInvitation.class);
		return userInvitationList;
	}

	@Override
	public List<UserInvitation> getByUserId(String userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		List<UserInvitation> userInvitationList = mongoTemplate.find(query, UserInvitation.class);
		return userInvitationList;
	}

	@Override
	public void deleteByContexId(String contextId) {
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
	public void deleteByUserId(String userId) {
		List<UserInvitation> userInvitationList = getByUserId(userId);
		if (userInvitationList != null) {
			for (UserInvitation userInvitation : userInvitationList) {
				if (userInvitation != null) {
					delete(userInvitation);
				}
			}
		}
	}

}
