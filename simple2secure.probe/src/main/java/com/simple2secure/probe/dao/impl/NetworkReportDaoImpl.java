package com.simple2secure.probe.dao.impl;

import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.probe.dao.NetworkReportDao;

public class NetworkReportDaoImpl extends BaseDaoImpl<NetworkReport> implements NetworkReportDao {

	public NetworkReportDaoImpl() {
		this.entityClass = NetworkReport.class;
	}
}
