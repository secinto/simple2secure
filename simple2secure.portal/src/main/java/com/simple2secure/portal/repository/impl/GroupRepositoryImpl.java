package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
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
	public List<CompanyGroup> findByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId)).with(Sort.by(Sort.Direction.ASC, "name"));
		return mongoTemplate.find(query, CompanyGroup.class, collectionName);
	}

	@Override
	public List<CompanyGroup> findRootGroupsByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId).and("rootGroup").is(true)).with(Sort.by(Sort.Direction.ASC, "name"));
		return mongoTemplate.find(query, CompanyGroup.class, collectionName);
	}

	@Override
	public List<CompanyGroup> findByParentId(ObjectId parentId) {
		Query query = new Query(Criteria.where("parentId").is(parentId)).with(Sort.by(Sort.Direction.ASC, "name"));
		return mongoTemplate.find(query, CompanyGroup.class, collectionName);
	}

	@Override
	public void deleteByContextId(ObjectId contextId) {
		List<CompanyGroup> groups = findByContextId(contextId);
		if (groups != null) {
			for (CompanyGroup group : groups) {
				if (group != null) {
					delete(group);
				}
			}
		}

	}

	@Override
	public CompanyGroup findStandardGroupByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId).and("standardGroup").is(true));
		return mongoTemplate.findOne(query, CompanyGroup.class, collectionName);
	}
}
