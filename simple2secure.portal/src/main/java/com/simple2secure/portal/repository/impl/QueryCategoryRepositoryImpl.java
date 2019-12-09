package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.QueryCategory;
import com.simple2secure.portal.repository.QueryCategoryRepository;

@Repository
@Transactional
public class QueryCategoryRepositoryImpl extends QueryCategoryRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "querycategory"; //$NON-NLS-1$
		super.className = QueryCategory.class;
	}
}
