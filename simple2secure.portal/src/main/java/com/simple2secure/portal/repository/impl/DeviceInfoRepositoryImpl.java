package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.portal.repository.DeviceInfoRepository;

@Repository
@Transactional
public class DeviceInfoRepositoryImpl extends DeviceInfoRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "deviceInfo"; //$NON-NLS-1$
		super.className = DeviceInfo.class;
	}

	@Override
	public DeviceInfo findByDeviceId(String deviceId) {
		Query query = new Query(Criteria.where("deviceId").is(deviceId));
		return mongoTemplate.findOne(query, DeviceInfo.class);
	}

}
