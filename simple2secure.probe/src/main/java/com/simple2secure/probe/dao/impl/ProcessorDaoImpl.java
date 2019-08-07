package com.simple2secure.probe.dao.impl;

import com.simple2secure.api.model.Processor;
import com.simple2secure.probe.dao.ProcessorDao;

public class ProcessorDaoImpl extends BaseDaoImpl<Processor> implements ProcessorDao {

	public ProcessorDaoImpl() {
		this.entityClass = Processor.class;
	}
}
