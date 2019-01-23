package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
	public List<Report> getReportsByProbeId(String probeId) {
		List<Report> reports = new ArrayList<>();
		Query query = new Query(Criteria.where("probeId").is(probeId));
		reports = mongoTemplate.find(query, Report.class, collectionName);
		return reports;
	}

	@Override
	public List<Report> getReportsByGroupId(String groupId) {
		List<Report> reports = new ArrayList<>();
		Query query = new Query(Criteria.where("groupId").is(groupId));
		reports = mongoTemplate.find(query, Report.class, collectionName);
		return reports;
	}

	@Override
	public void deleteByProbeId(String probeId) {
		List<Report> reports = getReportsByProbeId(probeId);

		if (reports != null) {
			for (Report report : reports) {
				delete(report);
			}
		}

	}

	@Override
	public List<Report> getReportsByName(String name) {
		List<Report> reports = new ArrayList<>();
		Query query = new Query(Criteria.where("query").is(name));
		reports = mongoTemplate.find(query, Report.class, collectionName);
		return reports;
	}

}
