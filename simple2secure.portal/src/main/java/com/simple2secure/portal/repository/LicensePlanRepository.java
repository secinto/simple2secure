package com.simple2secure.portal.repository;

import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class LicensePlanRepository extends MongoRepository<LicensePlan> {
	public abstract LicensePlan findByName(String name);
}
