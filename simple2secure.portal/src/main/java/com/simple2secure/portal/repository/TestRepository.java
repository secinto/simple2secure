package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.TestCase;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TestRepository extends MongoRepository<TestCase> {

	public abstract List<TestCase> getByToolId(String toolId);

}
