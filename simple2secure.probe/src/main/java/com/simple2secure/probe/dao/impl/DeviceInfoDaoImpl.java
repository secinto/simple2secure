package com.simple2secure.probe.dao.impl;

import com.google.common.base.Strings;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.probe.dao.DeviceInfoDao;

public class DeviceInfoDaoImpl extends BaseDaoImpl<DeviceInfo> implements DeviceInfoDao {
	public DeviceInfoDaoImpl(String persistenceUnitName) {
		entityClass = DeviceInfo.class;
		if (!Strings.isNullOrEmpty(persistenceUnitName)) {
			init(persistenceUnitName);
		} else {
			init(BaseDaoImpl.PERSISTENCE_UNIT_NAME);
		}
	}
}
