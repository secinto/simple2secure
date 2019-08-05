package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.TestResult;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TestResultRepository extends MongoRepository<TestResult> {

	public abstract List<TestResult> getByGroupId(String groupId);

	public abstract List<TestResult> getByLicenseId(String licenseId);

	public abstract List<TestResult> getByTestId(String testId);

	public abstract List<TestResult> getByTestRunId(String testRunId);

}
