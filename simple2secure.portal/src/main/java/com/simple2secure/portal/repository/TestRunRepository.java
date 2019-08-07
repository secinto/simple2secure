package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.TestRun;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TestRunRepository extends MongoRepository<TestRun> {

	public abstract List<TestRun> getPlannedTests(String podId);

	public abstract List<TestRun> getByContextId(String contextId);

	public abstract List<TestRun> getTestRunByPodId(String podId);
}
