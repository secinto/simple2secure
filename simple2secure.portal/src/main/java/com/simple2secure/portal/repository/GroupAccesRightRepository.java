package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.GroupAccessRight;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class GroupAccesRightRepository extends MongoRepository<GroupAccessRight> {
	public abstract List<GroupAccessRight> findByUserId(String userId);

	public abstract List<GroupAccessRight> findByGroupId(String groupId);

	public abstract List<GroupAccessRight> findByContextId(String contextId);

	public abstract List<GroupAccessRight> findByContextIdAndUserId(String contextId, String userId);

	public abstract GroupAccessRight findByGroupIdAndUserId(String groupId, String userId);

	public abstract void deleteByUserId(String userId);

	public abstract void deleteByGroupId(String groupId);

	public abstract void deleteByContextId(String contextId);

	public abstract void deleteByContextIdAndUserId(String contextId, String userId);

}
