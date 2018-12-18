package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestCaseResult;
import com.simple2secure.portal.repository.TestResultRepository;

@Repository
@Transactional
public class TestResultRepositoryImpl extends TestResultRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "testCaseResult"; //$NON-NLS-1$
		super.className = TestCaseResult.class;
	}

}
