package com.simple2secure.portal.repository;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class DeviceInfoRepository extends MongoRepository<DeviceInfo> {

	public abstract void delete(DeviceInfo item);

	public abstract DeviceInfo findByDeviceId(ObjectId deviceId);

	public abstract List<DeviceInfo> findByDeviceType(String type);

	public abstract List<Device> findByContextId(ObjectId contextId);

	public abstract List<Device> findByContextIdAndType(ObjectId contextId, String type, int page, int size, String filter);

	public abstract Map<String, Object> findAllPublicPodDevices(int page, int size, String filter);

}
