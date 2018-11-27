package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.Report;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class ReportRepository extends MongoRepository<Report> {
	public abstract List<Report> getReportsByProbeId(String probeId);

	public abstract List<Report> getReportsByGroupId(String groupId);

	public abstract void deleteByProbeId(String probeId);
}
