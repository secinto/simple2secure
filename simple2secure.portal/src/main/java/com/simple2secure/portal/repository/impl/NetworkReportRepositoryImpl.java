package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
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
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class NetworkReportRepositoryImpl extends NetworkReportRepository {
	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "networkReport"; //$NON-NLS-1$
		super.className = NetworkReport.class;
	}

	@Override
	public List<NetworkReport> getReportsByDeviceId(ObjectId deviceId) {
		List<NetworkReport> networkReports = new ArrayList<>();
		Query query = new Query(Criteria.where("deviceId").is(deviceId));
		networkReports = mongoTemplate.find(query, NetworkReport.class, collectionName);
		return networkReports;
	}

	@Override
	public void deleteByDeviceId(ObjectId deviceId) {
		List<NetworkReport> reports = getReportsByDeviceId(deviceId);

		if (reports != null) {
			for (NetworkReport report : reports) {
				delete(report);
			}
		}
	}

	@Override
	public List<NetworkReport> getSearchQueryByGroupId(String searchQuery, ObjectId groupId) {
		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(searchQuery);
		Query query = TextQuery.queryText(criteria).sortByScore();
		query.addCriteria(Criteria.where("groupId").is(groupId));
		List<NetworkReport> result = mongoTemplate.find(query, className, collectionName);
		return result;
	}

	@Override
	public List<NetworkReport> getSearchQueryByDeviceIds(String searchQuery, List<ObjectId> deviceIds) {
		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (ObjectId deviceId : deviceIds) {
			Criteria expression = new Criteria();
			expression.and("deviceId").is(deviceId);
			orExpression.add(expression);
		}

		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(searchQuery);
		query.addCriteria(criteria);

		List<NetworkReport> result = mongoTemplate.find(query, className, collectionName);
		return result;
	}

	@Override
	public NetworkReportDTO getReportsByDeviceIdWithPagination(List<ObjectId> deviceIds, int page, int size, String filter) {
		List<NetworkReport> reports = new ArrayList<>();
		long count = 0;
		NetworkReportDTO reportDTO = new NetworkReportDTO(reports, count);
		if (!deviceIds.isEmpty()) {

			List<Criteria> orExpression = new ArrayList<>();
			Criteria orCriteria = new Criteria();
			Query query = new Query();

			for (ObjectId deviceId : deviceIds) {
				Criteria expression = new Criteria();
				expression.and("deviceId").is(deviceId);
				orExpression.add(expression);
			}

			query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

			if (!Strings.isBlank(filter)) {
				query.addCriteria(Criteria.where("processorName").regex(filter, "i"));
			}

			count = mongoTemplate.count(query, OsQueryReport.class, collectionName);
			int limit = portalUtils.getPaginationLimit(size);
			int skip = portalUtils.getPaginationStart(size, page, limit);

			query.limit(limit);
			query.skip(skip);
			query.with(Sort.by(Sort.Direction.DESC, "startTime"));
			reports = mongoTemplate.find(query, NetworkReport.class, collectionName);
			reportDTO = new NetworkReportDTO(reports, count);
		}
		return reportDTO;
	}

}
