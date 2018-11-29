package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.portal.repository.GroupRepository;

@Repository
@Transactional
public class GroupRepositoryImpl extends GroupRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "companygroup";
		super.className = CompanyGroup.class;
	}

	@Override
	public List<CompanyGroup> findByOwnerId(String userId) {
		Query query = new Query(Criteria.where("addedByUserId").is(userId)).with(new Sort(Direction.ASC, "name"));
		return mongoTemplate.find(query, CompanyGroup.class, collectionName);
	}

	@Override
	public void deleteByOwnerId(String userId) {
		List<CompanyGroup> groups = findByOwnerId(userId);

		for (CompanyGroup group : groups) {
			mongoTemplate.remove(group);
		}

	}

	@Override
	public List<CompanyGroup> findByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId)).with(new Sort(Direction.ASC, "name"));
		return mongoTemplate.find(query, CompanyGroup.class, collectionName);
	}

	@Override
	public List<CompanyGroup> findRootGroupsByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId).and("rootGroup").is(true)).with(new Sort(Direction.ASC, "name"));
		return mongoTemplate.find(query, CompanyGroup.class, collectionName);
	}

	@Override
	public List<CompanyGroup> findByParentId(String parentId) {
		Query query = new Query(Criteria.where("parentId").is(parentId)).with(new Sort(Direction.ASC, "name"));
		return mongoTemplate.find(query, CompanyGroup.class, collectionName);
	}

	@Override
	public List<CompanyGroup> findBySuperUserId(String superUserId, String contextId) {
		List<CompanyGroup> groups = findByContextId(contextId);
		List<CompanyGroup> userGroups = new ArrayList<>();
		if (groups != null) {
			for (CompanyGroup group : groups) {
				if (group.getSuperUserIds().contains(superUserId)) {
					userGroups.add(group);
				}
			}
			return userGroups;
		}

		return userGroups;
	}
}
