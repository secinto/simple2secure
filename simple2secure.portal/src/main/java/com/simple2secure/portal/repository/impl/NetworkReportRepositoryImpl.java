package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;

@Repository
@Transactional
public class NetworkReportRepositoryImpl extends NetworkReportRepository {
	
	@Autowired
	LicenseRepository licenseRepository;
	
	@PostConstruct
	public void init() {
		super.collectionName = "networkReport"; //$NON-NLS-1$
		super.className = NetworkReport.class;
	}

	@Override
	public List<NetworkReport> getReportsByProbeId(String probeId) {
		List<NetworkReport> networkReports = new ArrayList<>();
		Query query = new Query(Criteria.where("probeId").is(probeId));
		networkReports = this.mongoTemplate.find(query, NetworkReport.class, this.collectionName);
		return networkReports;
	}

	@Override
	public List<NetworkReport> getReportsByUserID(String userId) {
		List<NetworkReport> reports = new ArrayList<>();
		List<CompanyLicensePrivate> licenses = this.licenseRepository.findByUserId(userId);
		
		if(licenses == null) {
			return null;
		}
		else {
			
			for(CompanyLicensePrivate license : licenses) {
				reports.addAll(getReportsByProbeId(license.getProbeId()));
			}
		}
		return reports;
	}

	@Override
	public void deleteByUserId(String userId) {
		List<NetworkReport> reports = getReportsByUserID(userId);
		
		if(reports != null) {
			for(NetworkReport report : reports) {
				delete(report);
			}
		}
		
	}

	@Override
	public void deleteByProbeId(String probeId) {
		List<NetworkReport> reports = getReportsByProbeId(probeId);
		
		if(reports != null) {
			for(NetworkReport report : reports) {
				delete(report);
			}
		}
		
	}
}
