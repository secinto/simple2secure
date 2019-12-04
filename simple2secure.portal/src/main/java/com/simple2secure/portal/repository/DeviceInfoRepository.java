package com.simple2secure.portal.repository;

import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class DeviceInfoRepository extends MongoRepository<DeviceInfo> {
	public abstract DeviceInfo findByDeviceId(String deviceId);
}
