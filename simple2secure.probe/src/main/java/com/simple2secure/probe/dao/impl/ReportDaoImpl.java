package com.simple2secure.probe.dao.impl;

import org.testng.util.Strings;

import com.simple2secure.api.model.Report;
import com.simple2secure.probe.dao.ReportDao;

public class ReportDaoImpl extends BaseDaoImpl<Report> implements ReportDao {

	public ReportDaoImpl(String persistenceUnitName) {
		entityClass = Report.class;
		if (!Strings.isNullOrEmpty(persistenceUnitName)) {
			init(persistenceUnitName);
		} else {
			init(BaseDaoImpl.PERSISTENCE_UNIT_NAME);
		}
	}

}
