package com.simple2secure.probe.dao.impl;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Step;
import com.simple2secure.probe.dao.StepDao;

public class StepDaoImpl extends BaseDaoImpl<Step> implements StepDao {

	public StepDaoImpl(String persistenceUnitName) {
		entityClass = Step.class;
		if (!Strings.isNullOrEmpty(persistenceUnitName)) {
			init(persistenceUnitName);
		} else {
			init(BaseDaoImpl.PERSISTENCE_UNIT_NAME);
		}
	}

}
