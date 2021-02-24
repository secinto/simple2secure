package com.simple2secure.portal.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.dto.TestSequenceRunDTO;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.SequenceRunRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class SequenceRunRepositoryImpl extends SequenceRunRepository {

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "sequenceRun"; //$NON-NLS-1$
		super.className = SequenceRun.class;
	}

	@Override
	public List<SequenceRun> getSequenceRunByDeviceId(ObjectId deviceId) {
		Query query = new Query(Criteria.where("deviceId").is(deviceId));
		List<SequenceRun> sequences = mongoTemplate.find(query, SequenceRun.class);
		return sequences;
	}

	@Override
	public Map<String, Object> getByContextIdWithPagination(ObjectId contextId, int page, int size, String filter) {
		AggregationOperation matchContextId = Aggregation.match(new Criteria("_id").is(contextId));
		AggregationOperation lookUpRuns = Aggregation.lookup("sequenceRun", "_id", "contextId", "sequenceRun");
		AggregationOperation unwindRuns = Aggregation.unwind("sequenceRun", true);
		AggregationOperation lookUpResults = Aggregation.lookup("testSequenceResult", "sequenceRun._id", "sequenceRunId", "sequenceResult");
		AggregationOperation unwindResults = Aggregation.unwind("sequenceResult", true);
		AggregationOperation countTotal = Aggregation.count().as(StaticConfigItems.COUNT_FIELD);

		String[] filterFields = { "sequenceRun.sequenceName", "sequenceRun.sequenceRunType", "sequenceRun.sequenceStatus" };
		AggregationOperation filtering = Aggregation.match(defineFilterCriteriaWithManyFields(filterFields, filter));

		Aggregation aggregation = Aggregation.newAggregation(Context.class, matchContextId, lookUpRuns, unwindRuns, lookUpResults,
				unwindResults, countTotal);
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(Context.class, matchContextId, lookUpRuns, unwindRuns, lookUpResults, unwindResults,
					filtering, countTotal);
		}

		Object count = getCountResult(mongoTemplate.aggregate(aggregation, "context", Object.class));

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		AggregationOperation paginationLimit = Aggregation.limit(limit);
		AggregationOperation paginationSkip = Aggregation.skip(skip);
		AggregationOperation sort = Aggregation.sort(Sort.Direction.DESC, "sequenceRun.timestamp");

		aggregation = Aggregation.newAggregation(Context.class, matchContextId, lookUpRuns, unwindRuns, lookUpResults, unwindResults, sort,
				paginationSkip, paginationLimit);

		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(Context.class, matchContextId, lookUpRuns, unwindRuns, lookUpResults, unwindResults,
					filtering, sort, paginationSkip, paginationLimit);
		}

		AggregationResults<TestSequenceRunDTO> results = mongoTemplate.aggregate(aggregation, "context", TestSequenceRunDTO.class);
		List<TestSequenceRunDTO> tests = results.getMappedResults();

		Map<String, Object> testRunObject = new HashMap<>();
		testRunObject.put("sequences", tests);
		testRunObject.put("totalSize", count);

		return testRunObject;
	}
}
