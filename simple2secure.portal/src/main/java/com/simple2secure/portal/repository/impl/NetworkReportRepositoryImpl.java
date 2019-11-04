package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.dto.NetworkReportDTO;
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
	public List<NetworkReport> getReportsByDeviceId(String probeId) {
		List<NetworkReport> networkReports = new ArrayList<>();
		Query query = new Query(Criteria.where("probeId").is(probeId));
		networkReports = mongoTemplate.find(query, NetworkReport.class, collectionName);
		return networkReports;
	}

	@Override
	public void deleteByDeviceId(String probeId) {
		List<NetworkReport> reports = getReportsByDeviceId(probeId);

		if (reports != null) {
			for (NetworkReport report : reports) {
				delete(report);
			}
		}
	}

	@Override
	public List<NetworkReport> getReportsByName(String name) {
		List<NetworkReport> reports = new ArrayList<>();
		Query query = new Query(Criteria.where("processorName").is(name));
		reports = mongoTemplate.find(query, NetworkReport.class, collectionName);
		return reports;
	}

	@Override
	public List<NetworkReport> getSearchQueryByGroupId(String searchQuery, String groupId) {
		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(searchQuery);
		Query query = TextQuery.queryText(criteria).sortByScore();
		query.addCriteria(Criteria.where("groupId").is(groupId));
		List<NetworkReport> result = mongoTemplate.find(query, className, collectionName);
		return result;
	}

	@Override
	public NetworkReportDTO getReportsByGroupId(List<String> group_ids, int limit) {
		List<NetworkReport> reports = new ArrayList<>();
		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (String groupId : group_ids) {
			Criteria expression = new Criteria();
			expression.and("groupId").is(groupId);
			orExpression.add(expression);
		}
		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

		long count = mongoTemplate.count(query, NetworkReport.class, collectionName);

		query.limit(limit);
		reports = mongoTemplate.find(query, NetworkReport.class, collectionName);

		NetworkReportDTO reportDTO = new NetworkReportDTO(reports, count);
		return reportDTO;
	}

}
