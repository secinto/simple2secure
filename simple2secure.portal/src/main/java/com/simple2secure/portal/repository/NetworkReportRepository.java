package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class NetworkReportRepository extends MongoRepository<NetworkReport> {

	public abstract List<NetworkReport> getReportsByProbeId(String probeId);

	public abstract List<NetworkReport> getReportsByGroupId(String groupId);

	public abstract void deleteByProbeId(String probeId);

	public abstract List<NetworkReport> getReportsByName(String name);
}
