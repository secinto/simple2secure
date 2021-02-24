package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.SystemUnderTestRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class SystemUnderTestRepositoryImpl extends SystemUnderTestRepository {

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "systemUnderTest";
		super.className = SystemUnderTest.class;
	}

	@Override
	public Map<String, Object> getAllByContextIdPaged(ObjectId contextId, int page, int size, String filter) {
		
		AggregationOperation matchContext = Aggregation.match(new Criteria("contextId").is(contextId));
		AggregationOperation countTotal = Aggregation.count().as(StaticConfigItems.COUNT_FIELD);
		
		String[] filterFields = {"name"};
		AggregationOperation filtering = Aggregation.match(defineFilterCriteriaWithManyFields(filterFields, filter));
		
		Aggregation aggregation = Aggregation.newAggregation(SystemUnderTest.class, matchContext, countTotal);
		
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(SystemUnderTest.class, matchContext, filtering, countTotal);
		}
		
		Object count = getCountResult(mongoTemplate.aggregate(aggregation, this.collectionName, Object.class));
		
		int limit = portalUtils.getPaginationLimit(size);
		long skip = portalUtils.getPaginationStart(size, page, limit);
		
		AggregationOperation paginationLimit = Aggregation.limit(limit);
		AggregationOperation paginationSkip = Aggregation.skip(skip);
		
		aggregation = Aggregation.newAggregation(SystemUnderTest.class, matchContext, paginationSkip, paginationLimit);
		
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(SystemUnderTest.class, matchContext, filtering, paginationSkip, paginationLimit);
		}
		
		AggregationResults<SystemUnderTest> results = mongoTemplate.aggregate(aggregation, this.collectionName, SystemUnderTest.class);
		
		Map<String, Object> sutMap = new HashMap<>();
		sutMap.put("sutList", results.getMappedResults());
		sutMap.put("totalSize", count);
		
		return sutMap;
	}

	@Override
	public List<SystemUnderTest> getApplicableSystemUnderTests(List<String> sutMetadataKeyList) {
		List<Criteria> criterias = new ArrayList<>();
		Criteria[] criteriaArr = new Criteria[criterias.size()];
		Criteria criteria = new Criteria();
		for (String key : sutMetadataKeyList) {
			criterias.add(new Criteria("metadata." + key).exists(true));
		}
		criteria = criteria.andOperator(criterias.toArray(criteriaArr));

		MatchOperation matchStage = Aggregation.match(criteria);
		Aggregation aggregation = Aggregation.newAggregation(matchStage);
		AggregationResults<SystemUnderTest> result = mongoTemplate.aggregate(aggregation, "systemUnderTest", SystemUnderTest.class);
		return result.getMappedResults();
	}

	@Override
	public List<SystemUnderTest> getAllByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<SystemUnderTest> suts = mongoTemplate.find(query, SystemUnderTest.class);
		return suts;
	}

}