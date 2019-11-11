package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.portal.repository.SystemUnderTestRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class SystemUnderTestRepositoryImpl extends SystemUnderTestRepository {

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "systemUnderTest";
		super.className = SystemUnderTest.class;
	}

	@Override
	public List<SystemUnderTest> getByGroupId(String groupId, int page, int size) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);
		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
		List<SystemUnderTest> sutList = mongoTemplate.find(query, SystemUnderTest.class);
		return sutList;
	}

	@Override
	public SystemUnderTest getByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		SystemUnderTest sut = mongoTemplate.findOne(query, SystemUnderTest.class);
		return sut;
	}

	@Override
	public long getCountOfSUTWithGroupId(String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		long count = mongoTemplate.count(query, SystemUnderTest.class, collectionName);
		return count;
	}

}