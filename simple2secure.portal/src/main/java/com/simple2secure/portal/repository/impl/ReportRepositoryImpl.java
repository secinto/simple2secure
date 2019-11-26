package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.Date;
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

import com.simple2secure.api.dto.ReportDTO;
import com.simple2secure.api.model.Report;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.ReportRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class ReportRepositoryImpl extends ReportRepository {

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "report"; //$NON-NLS-1$
		super.className = Report.class;
	}

	@Override
	public List<Report> getReportsByDeviceId(String deviceId) {
		List<Report> reports = new ArrayList<>();
		Query query = new Query(Criteria.where("probeId").is(deviceId));
		reports = mongoTemplate.find(query, Report.class, collectionName);
		return reports;
	}

	@Override
	public ReportDTO getReportsByGroupId(List<String> group_ids, int page, int size) {
		List<Report> reports = new ArrayList<>();
		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (String groupId : group_ids) {
			Criteria expression = new Criteria();
			expression.and("groupId").is(groupId);
			orExpression.add(expression);
		}
		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

		long count = mongoTemplate.count(query, Report.class, collectionName);
		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "queryTimestamp"));
		reports = mongoTemplate.find(query, Report.class, collectionName);

		ReportDTO reportDTO = new ReportDTO(reports, count);
		return reportDTO;
	}

	@Override
	public void deleteByDeviceId(String deviceId) {
		List<Report> reports = getReportsByDeviceId(deviceId);

		if (reports != null) {
			for (Report report : reports) {
				delete(report);
			}
		}

	}

	@Override
	public List<Report> getReportsByName(String name, int page, int size) {

		List<Report> reports = new ArrayList<>();

		Query query = new Query(Criteria.where("name").is(name));

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.ASC, "queryTimestamp"));
		reports = mongoTemplate.find(query, Report.class, collectionName);
		return reports;
	}

	@Override
	public long getPagesForReportsByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		long count = mongoTemplate.count(query, Report.class, collectionName) / StaticConfigItems.DEFAULT_VALUE_SIZE;
		return count;
	}

	@Override
	public List<Report> getSearchQueryByGroupId(String searchQuery, String groupId) {
		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(searchQuery);
		Query query = TextQuery.queryText(criteria).sortByScore();
		query.addCriteria(Criteria.where("groupId").is(groupId));
		List<Report> result = mongoTemplate.find(query, className, collectionName);
		return result;

	}

	@Override
	public List<Report> getLastReportsFromTimeStampAndName(Date timestamp, String name) {
		Query query = new Query(Criteria.where("queryTimestamp").lt(timestamp).and("name").is("name"));

		query.limit(2);
		query.skip(0);
		query.with(Sort.by(Sort.Direction.ASC, "queryTimestamp"));
		List<Report> result = mongoTemplate.find(query, className, collectionName);
		return result;
	}

}
