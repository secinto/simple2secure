package com.simple2secure.probe.dao.impl;

import org.testng.util.Strings;

import com.simple2secure.api.model.QueryRun;
import com.simple2secure.probe.dao.QueryDao;

public class QueryDaoImpl extends BaseDaoImpl<QueryRun> implements QueryDao {

	public QueryDaoImpl(String persistenceUnitName) {
		entityClass = QueryRun.class;
		if (!Strings.isNullOrEmpty(persistenceUnitName)) {
			init(persistenceUnitName);
		} else {
			init(BaseDaoImpl.PERSISTENCE_UNIT_NAME);
		}
	}

}
