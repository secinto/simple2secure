package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Test;
import com.simple2secure.portal.repository.TestRepository;

@Repository
@Transactional
public class TestRepositoryImpl extends TestRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "test"; //$NON-NLS-1$
		super.className = Test.class;
	}	
}
