package com.simple2secure.probe.dao.impl;

import com.simple2secure.api.model.QueryRun;
import com.simple2secure.probe.dao.QueryDao;

public class QueryDaoImpl extends BaseDaoImpl<QueryRun> implements QueryDao {

	public QueryDaoImpl() {
		this.entityClass = QueryRun.class;
	}
}
