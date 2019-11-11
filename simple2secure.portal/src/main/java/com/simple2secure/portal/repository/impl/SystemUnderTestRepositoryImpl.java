package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.portal.repository.SystemUnderTestRepository;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SystemUnderTestRepositoryImpl extends SystemUnderTestRepository {


    @PostConstruct
    public void init() {
        super.collectionName = "systemUnderTest";
        super.className = SystemUnderTest.class;
    }

    @Override
    public List<SystemUnderTest> getByGroupId(String groupId) {
        Query query = new Query(Criteria.where("groupId").is(groupId));
        List<SystemUnderTest> sutList = mongoTemplate.find(query, SystemUnderTest.class);
        return sutList;
    }

    @Override
    public SystemUnderTest getByName(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        SystemUnderTest sut = mongoTemplate.findOne(query, SystemUnderTest.class);
        return sut;
    }

}