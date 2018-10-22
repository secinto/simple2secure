package com.simple2secure.portal.repository;
import java.util.List;
import com.simple2secure.api.model.CompanyLicense;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class LicenseRepository extends MongoRepository<CompanyLicense> {
	public abstract List<CompanyLicense> findByGroupId(String groupId);
	
	public abstract List<CompanyLicense> findByUserId(String userId);
	
	public abstract CompanyLicense findByProbeId(String probeId);
	
	public abstract CompanyLicense findByAccessToken(String accessToken);
	
	public abstract CompanyLicense findByGroupAndUserId(String groupId, String userId);
	
	public abstract void deleteByGroupId(String groupId);
	
}
