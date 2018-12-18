package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.TestCaseResult;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TestResultRepository extends MongoRepository<TestCaseResult> {

	public abstract List<TestCaseResult> findByToolId(String toolId);

}
