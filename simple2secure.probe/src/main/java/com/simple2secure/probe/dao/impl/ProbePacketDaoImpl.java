package com.simple2secure.probe.dao.impl;

import com.google.common.base.Strings;
import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.probe.dao.ProbePacketDao;

public class ProbePacketDaoImpl extends BaseDaoImpl<ProbePacket> implements ProbePacketDao {

	public ProbePacketDaoImpl(String persistenceUnitName) {
		entityClass = ProbePacket.class;
		if (!Strings.isNullOrEmpty(persistenceUnitName)) {
			init(persistenceUnitName);
		} else {
			init(BaseDaoImpl.PERSISTENCE_UNIT_NAME);
		}
	}

}
