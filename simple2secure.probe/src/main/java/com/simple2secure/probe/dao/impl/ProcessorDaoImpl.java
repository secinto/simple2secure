package com.simple2secure.probe.dao.impl;

import org.testng.util.Strings;

import com.simple2secure.api.model.Processor;
import com.simple2secure.probe.dao.ProcessorDao;

public class ProcessorDaoImpl extends BaseDaoImpl<Processor> implements ProcessorDao {

	public ProcessorDaoImpl(String persistenceUnitName) {
		entityClass = Processor.class;
		if (!Strings.isNullOrEmpty(persistenceUnitName)) {
			init(persistenceUnitName);
		} else {
			init(BaseDaoImpl.PERSISTENCE_UNIT_NAME);
		}
	}

}
