package com.simple2secure.portal.repository.impl;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

//import javax.annotation.PostConstruct;

import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.portal.repository.TestSequenceResultRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class TestSequenceResultRepositoryImpl extends TestSequenceResultRepository {
	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "testSequenceResult"; //$NON-NLS-1$
		super.className = TestSequenceResult.class;
	}

	@Override
	public List<TestSequenceResult> getBySequenceId(ObjectId sequenceId) {
		Query query = new Query(Criteria.where("sequenceId").is(sequenceId));
		List<TestSequenceResult> testSequenceResults = mongoTemplate.find(query, TestSequenceResult.class);
		return testSequenceResults;
	}

	@Override
	public TestSequenceResult getBySequenceRunId(ObjectId sequenceRunId) {
		Query query = new Query(Criteria.where("sequenceRunId").is(sequenceRunId));
		TestSequenceResult testSequenceResults = mongoTemplate.findOne(query, TestSequenceResult.class);
		return testSequenceResults;
	}

	@Override
	public List<TestSequenceResult> getByDeviceId(ObjectId deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId));
		List<TestSequenceResult> result = mongoTemplate.find(query, TestSequenceResult.class);
		return result;
	}

	@Override
	public Map<String, Object> getSequenceResultsByDeviceIdWithPagination(List<ObjectId> deviceIds, int page, int size, String filter) {
		long count = 0;
		Map<String, Object> testResultMap = new HashMap<>();
		if (!deviceIds.isEmpty()) {

			List<Criteria> orExpression = new ArrayList<>();
			Criteria orCriteria = new Criteria();
			Query query = new Query();

			for (ObjectId deviceId : deviceIds) {
				Criteria expression = new Criteria();
				expression.and("podId").is(deviceId);
				orExpression.add(expression);
			}

			query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

			if (!Strings.isBlank(filter)) {
				query.addCriteria(Criteria.where("sequenceName").regex(filter, "i"));
			}

			count = mongoTemplate.count(query, TestSequenceResult.class, collectionName);
			int limit = portalUtils.getPaginationLimit(size);
			int skip = portalUtils.getPaginationStart(size, page, limit);

			query.limit(limit);
			query.skip(skip);
			query.with(Sort.by(Sort.Direction.DESC, "timestamp"));

			List<TestSequenceResult> reports = mongoTemplate.find(query, TestSequenceResult.class, collectionName);

			testResultMap.put("report", reports);
			testResultMap.put("totalSize", count);
		}else {
			testResultMap.put("report", new ArrayList<TestSequenceResult>());
			testResultMap.put("totalSize", 0);
		}
		return testResultMap;
	}

}
