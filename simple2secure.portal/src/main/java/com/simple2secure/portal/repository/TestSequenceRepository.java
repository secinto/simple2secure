package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.TestSequence;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TestSequenceRepository extends MongoRepository<TestSequence> {

	public abstract List<TestSequence> getByPodId(String podId);

	// public abstract List<TestSequence> getByHostname(String hostname);

	// public abstract List<Test> getScheduledTest();

	public abstract TestSequence getSequenceByName(String name);

	public abstract TestSequence getSequenceByNameAndPodId(String name, String podId);

}
