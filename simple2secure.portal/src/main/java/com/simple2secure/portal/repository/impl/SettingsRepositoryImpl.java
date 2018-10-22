package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.simple2secure.api.model.Settings;
import com.simple2secure.portal.repository.SettingsRepository;

@Repository
@Transactional
public class SettingsRepositoryImpl extends SettingsRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "settings";
		super.className = Settings.class;
	}
}
