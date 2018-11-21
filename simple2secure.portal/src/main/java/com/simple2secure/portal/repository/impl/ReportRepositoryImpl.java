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
import com.simple2secure.api.model.Report;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.ReportRepository;

@Repository
@Transactional
public class ReportRepositoryImpl extends ReportRepository {
	
	@Autowired
	LicenseRepository licenseRepository;
	
	@PostConstruct
	public void init() {
		super.collectionName = "report"; //$NON-NLS-1$
		super.className = Report.class;
	}

	@Override
	public List<Report> getAllReportsByUserID(String userId) {
		List<Report> reports = new ArrayList<>();
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
	public List<Report> getReportsByProbeId(String probeId) {
		List<Report> reports = new ArrayList<>();
		Query query = new Query(Criteria.where("probeId").is(probeId));
		reports = this.mongoTemplate.find(query, Report.class, this.collectionName);
		return reports;
	}

	@Override
	public void deleteByUserId(String userId) {
		List<Report> reports = getAllReportsByUserID(userId);
		
		if(reports != null) {
			for(Report report : reports) {
				delete(report);
			}
		}	
	}

	@Override
	public void deleteByProbeId(String probeId) {
		List<Report> reports = getReportsByProbeId(probeId);
		
		if(reports != null) {
			for(Report report : reports) {
				delete(report);
			}
		}
		
	}
	
	
	
	
}
