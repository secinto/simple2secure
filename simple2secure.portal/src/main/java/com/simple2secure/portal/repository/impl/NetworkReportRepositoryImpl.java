package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class NetworkReportRepositoryImpl extends NetworkReportRepository {

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "networkReport"; //$NON-NLS-1$
		super.className = NetworkReport.class;
	}

	@Override
	public List<NetworkReport> getReportsByDeviceId(String deviceId) {
		List<NetworkReport> networkReports = new ArrayList<>();
		Query query = new Query(Criteria.where("probeId").is(deviceId));
		networkReports = mongoTemplate.find(query, NetworkReport.class, collectionName);
		return networkReports;
	}

	@Override
	public void deleteByDeviceId(String deviceId) {
		List<NetworkReport> reports = getReportsByDeviceId(deviceId);

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
	public NetworkReportDTO getReportsByGroupId(List<String> group_ids, int size, int page) {
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

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "startTime"));
		reports = mongoTemplate.find(query, NetworkReport.class, collectionName);

		NetworkReportDTO reportDTO = new NetworkReportDTO(reports, count);
		return reportDTO;
	}

}
