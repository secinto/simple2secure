package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class GroupRepository extends MongoRepository<CompanyGroup> {
	public abstract List<CompanyGroup> findByParentId(String parentId);

	public abstract List<CompanyGroup> findByContextId(String contextId);

	public abstract List<CompanyGroup> findRootGroupsByContextId(String contextId);
}
