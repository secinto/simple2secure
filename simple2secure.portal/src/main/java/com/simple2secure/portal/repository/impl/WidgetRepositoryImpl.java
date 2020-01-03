package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Widget;
import com.simple2secure.portal.repository.WidgetRepository;

@Repository
@Transactional
public class WidgetRepositoryImpl extends WidgetRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "widget"; //$NON-NLS-1$
		super.className = Widget.class;
	}
}
