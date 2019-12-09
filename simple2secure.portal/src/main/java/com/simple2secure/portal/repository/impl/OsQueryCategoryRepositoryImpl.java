package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.OsQueryCategory;
import com.simple2secure.portal.repository.OsQueryCategoryRepository;

@Repository
@Transactional
public class OsQueryCategoryRepositoryImpl extends OsQueryCategoryRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "osquerycategory"; //$NON-NLS-1$
		super.className = OsQueryCategory.class;
	}
}
