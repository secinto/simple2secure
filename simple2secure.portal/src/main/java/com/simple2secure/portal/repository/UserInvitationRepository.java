package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.UserInvitation;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class UserInvitationRepository extends MongoRepository<UserInvitation> {
	public abstract UserInvitation getByInvitationToken(String invitationToken);

	public abstract List<UserInvitation> getByContextId(String contextId);

	public abstract List<UserInvitation> getByUserId(String userId);

	public abstract void deleteByContexId(String contextId);

	public abstract void deleteByUserId(String userId);
}
