package com.simple2secure.probe.dao.impl;

import com.simple2secure.api.model.Report;
import com.simple2secure.probe.dao.ReportDao;

public class ReportDaoImpl extends BaseDaoImpl<Report> implements ReportDao {

	public ReportDaoImpl() {
		this.entityClass = Report.class;
	}
}
