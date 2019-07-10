package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.Test;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TestRepository extends MongoRepository<Test> {

	public abstract List<Test> getByPodId(String podId);

	public abstract List<Test> getByHostname(String hostname);

	public abstract List<Test> getScheduledTest();

	public abstract Test getTestByName(String name);

	public abstract Test getTestByNameAndPodId(String name, String podId);

}
