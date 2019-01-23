package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.TestCaseTemplate;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TestTemplateRepository extends MongoRepository<TestCaseTemplate> {

	public abstract List<TestCaseTemplate> getByToolId(String toolId);

}
