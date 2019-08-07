package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class NetworkReportRepository extends MongoRepository<NetworkReport> {
	
	public abstract List<NetworkReport> getReportsByProbeId(String probeId);
	
	public abstract List<NetworkReport> getReportsByUserID(String userId);
	
	public abstract void deleteByUserId(String userId);
	
	public abstract void deleteByProbeId(String probeId);
}
