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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.dto.OsQueryReportDTO;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.OsQueryReportRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class OsQueryReportRepositoryImpl extends OsQueryReportRepository {

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "osQueryReport"; //$NON-NLS-1$
		super.className = OsQueryReport.class;
	}

	@Override
	public List<OsQueryReport> getReportsByDeviceId(String deviceId) {
		List<OsQueryReport> reports = new ArrayList<>();
		Query query = new Query(Criteria.where("probeId").is(deviceId));
		reports = mongoTemplate.find(query, OsQueryReport.class, collectionName);
		return reports;
	}

	@Override
	public OsQueryReportDTO getReportsByDeviceId(List<String> deviceIds, int page, int size) {
		List<OsQueryReport> reports = new ArrayList<>();
		long count = 0;
		OsQueryReportDTO reportDTO = new OsQueryReportDTO(reports, count);
		if (!deviceIds.isEmpty()) {
			List<Criteria> orExpression = new ArrayList<>();
			Criteria orCriteria = new Criteria();
			Query query = new Query();
			for (String deviceId : deviceIds) {
				Criteria expression = new Criteria();
				expression.and("deviceId").is(deviceId);
				orExpression.add(expression);
			}

			query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

			count = mongoTemplate.count(query, OsQueryReport.class, collectionName);
			int limit = portalUtils.getPaginationLimit(size);
			int skip = portalUtils.getPaginationStart(size, page, limit);

			query.limit(limit);
			query.skip(skip);
			query.with(Sort.by(Sort.Direction.DESC, "queryTimestamp"));
			reports = mongoTemplate.find(query, OsQueryReport.class, collectionName);
			reportDTO = new OsQueryReportDTO(reports, count);
		}
		return reportDTO;
	}

	@Override
	public void deleteByDeviceId(String deviceId) {
		List<OsQueryReport> reports = getReportsByDeviceId(deviceId);

		if (reports != null) {
			for (OsQueryReport report : reports) {
				delete(report);
			}
		}

	}

	@Override
	public List<OsQueryReport> getReportsByName(String name, int page, int size) {

		List<OsQueryReport> reports = new ArrayList<>();

		Query query = new Query(Criteria.where("name").is(name));

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.ASC, "queryTimestamp"));
		reports = mongoTemplate.find(query, OsQueryReport.class, collectionName);
		return reports;
	}

	@Override
	public List<OsQueryReport> getReportsByDeviceAndName(String deviceId, String name, int page, int size) {

		List<OsQueryReport> reports = new ArrayList<>();

		Query query = new Query(Criteria.where("deviceId").is(deviceId).and("name").is(name));

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.ASC, "queryTimestamp"));
		reports = mongoTemplate.find(query, OsQueryReport.class, collectionName);
		return reports;
	}

	@Override
	public long getPagesForReportsByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		long count = mongoTemplate.count(query, OsQueryReport.class, collectionName) / StaticConfigItems.DEFAULT_VALUE_SIZE;
		return count;
	}

	@Override
	public long getPagesForReportsByDeviceAndName(String deviceId, String name) {
		Query query = new Query(Criteria.where("deviceId").is(deviceId).and("name").is(name));
		long count = mongoTemplate.count(query, OsQueryReport.class, collectionName) / StaticConfigItems.DEFAULT_VALUE_SIZE;
		return count;
	}

	@Override
	public List<OsQueryReport> getSearchQueryByDeviceIds(String searchQuery, List<String> deviceIds) {

		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (String deviceId : deviceIds) {
			Criteria expression = new Criteria();
			expression.and("deviceId").is(deviceId);
			orExpression.add(expression);
		}

		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(searchQuery);
		query.addCriteria(criteria);
		List<OsQueryReport> result = mongoTemplate.find(query, className, collectionName);
		return result;

	}

	@Override
	public List<OsQueryReport> getLastReportsFromTimeStampAndName(Date timestamp, String name) {
		Query query = new Query(Criteria.where("queryTimestamp").lt(timestamp).and("name").is("name"));

		query.limit(2);
		query.skip(0);
		query.with(Sort.by(Sort.Direction.ASC, "queryTimestamp"));
		List<OsQueryReport> result = mongoTemplate.find(query, className, collectionName);
		return result;
	}

}
