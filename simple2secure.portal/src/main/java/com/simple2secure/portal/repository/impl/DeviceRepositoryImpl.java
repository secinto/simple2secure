package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.simple2secure.api.model.Probe;
import com.simple2secure.portal.repository.DeviceRepository;

@Repository
@Transactional
public class DeviceRepositoryImpl extends DeviceRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "device";
		super.className = Probe.class;
	}

	@Override
	public Probe findByProbeAndUserId(String probeId, String userId) {
		Query query = new Query(Criteria.where("_id").is(new ObjectId(probeId)).and("userId").is(userId));
		return this.mongoTemplate.findOne(query, Probe.class);
	}
}
