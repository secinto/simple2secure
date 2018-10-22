package com.simple2secure.portal.repository;

import com.simple2secure.api.model.Probe;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class DeviceRepository extends MongoRepository<Probe> {
	public abstract Probe findByProbeAndUserId(String deviceId, String userId);
}
