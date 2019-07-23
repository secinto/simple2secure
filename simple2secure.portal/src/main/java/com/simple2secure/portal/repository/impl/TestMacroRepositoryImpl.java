package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestMacro;
import com.simple2secure.portal.repository.TestMacroRepository;

@Repository
@Transactional
public class TestMacroRepositoryImpl extends TestMacroRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "testMacro"; //$NON-NLS-1$
		super.className = TestMacro.class;
	}
}
