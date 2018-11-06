package com.simple2secure.portal.repository;

import com.simple2secure.api.model.AdminGroup;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class AdminGroupRepository extends MongoRepository<AdminGroup> {	
	public abstract AdminGroup getAdminGroupByUserId(String userId);
	public abstract AdminGroup deleteByAdminGroupId(String adminGroupId);
}
