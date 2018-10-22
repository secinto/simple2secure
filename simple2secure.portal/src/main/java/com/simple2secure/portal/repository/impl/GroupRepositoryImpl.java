package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

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
		Query query = new Query(Criteria.where("addedByUserId").is(userId));
		return this.mongoTemplate.find(query, CompanyGroup.class, this.collectionName);
	}

	@Override
	public void deleteByOwnerId(String userId) {
		List<CompanyGroup> groups = findByOwnerId(userId);
		
		for(CompanyGroup group : groups) {
			this.mongoTemplate.remove(group);
		}
		
	}
}
