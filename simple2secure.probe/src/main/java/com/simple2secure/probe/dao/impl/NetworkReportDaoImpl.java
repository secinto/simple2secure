package com.simple2secure.probe.dao.impl;

import org.testng.util.Strings;

import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.probe.dao.NetworkReportDao;

public class NetworkReportDaoImpl extends BaseDaoImpl<NetworkReport> implements NetworkReportDao {

	public NetworkReportDaoImpl(String persistenceUnitName) {
		entityClass = NetworkReport.class;
		if (!Strings.isNullOrEmpty(persistenceUnitName)) {
			init(persistenceUnitName);
		} else {
			init(BaseDaoImpl.PERSISTENCE_UNIT_NAME);
		}
	}

}
