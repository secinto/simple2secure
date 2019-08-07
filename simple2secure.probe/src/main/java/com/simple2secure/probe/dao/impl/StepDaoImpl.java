package com.simple2secure.probe.dao.impl;

import com.simple2secure.api.model.Step;
import com.simple2secure.probe.dao.StepDao;

public class StepDaoImpl extends BaseDaoImpl<Step> implements StepDao {

	public StepDaoImpl() {
		this.entityClass = Step.class;
	}
}
