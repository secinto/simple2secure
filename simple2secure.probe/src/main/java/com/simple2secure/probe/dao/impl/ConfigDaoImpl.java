package com.simple2secure.probe.dao.impl;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Config;
import com.simple2secure.probe.dao.ConfigDao;

public class ConfigDaoImpl extends BaseDaoImpl<Config> implements ConfigDao {

	public ConfigDaoImpl(String persistenceUnitName) {
		entityClass = Config.class;
		if (!Strings.isNullOrEmpty(persistenceUnitName)) {
			init(persistenceUnitName);
		} else {
			init(BaseDaoImpl.PERSISTENCE_UNIT_NAME);
		}
	}
}
