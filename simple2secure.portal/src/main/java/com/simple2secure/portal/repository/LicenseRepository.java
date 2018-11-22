package com.simple2secure.portal.repository;
import java.util.List;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class LicenseRepository extends MongoRepository<CompanyLicensePrivate> {
	public abstract List<CompanyLicensePrivate> findByGroupId(String groupId);
	
	public abstract List<CompanyLicensePrivate> findByUserId(String userId);
	
	public abstract CompanyLicensePrivate findByProbeId(String probeId);
	
	public abstract CompanyLicensePrivate findByAccessToken(String accessToken);
	
	public abstract CompanyLicensePrivate findByGroupAndUserId(String groupId, String userId);
	
	public abstract void deleteByGroupId(String groupId);
	
}
