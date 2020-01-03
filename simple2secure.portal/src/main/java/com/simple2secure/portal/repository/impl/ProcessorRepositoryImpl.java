package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Processor;
import com.simple2secure.portal.repository.ProcessorRepository;

@Repository
@Transactional
public class ProcessorRepositoryImpl extends ProcessorRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "processor"; //$NON-NLS-1$
		super.className = Processor.class;
	}
}
