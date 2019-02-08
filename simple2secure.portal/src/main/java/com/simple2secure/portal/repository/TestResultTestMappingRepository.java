package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.TestResultTestMapping;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TestResultTestMappingRepository extends MongoRepository<TestResultTestMapping> {

	public abstract List<TestResultTestMapping> getByTestId(String testId);

	public abstract List<TestResultTestMapping> getByToolId(String toolId);

	public abstract void deleteByTestId(String testId);

}
