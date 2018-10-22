package com.simple2secure.probe.dao.impl;

import com.simple2secure.api.model.Config;
import com.simple2secure.probe.dao.ConfigDao;

public class ConfigDaoImpl extends BaseDaoImpl<Config> implements ConfigDao {

	public ConfigDaoImpl() {
		entityClass = Config.class;
	}
}
