package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.portal.repository.LicensePlanRepository;

@Repository
@Transactional
public class LicensePlanRepositoryImpl extends LicensePlanRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "licensePlan"; //$NON-NLS-1$
		super.className = LicensePlan.class;
	}

	@Override
	public LicensePlan findByName(String name) {
		List<LicensePlan> licensePlans = this.mongoTemplate.findAll(LicensePlan.class);
		for (LicensePlan lp : licensePlans) {
			if (lp.getName().contains(name)) {
				return lp;
			}
		}
		return null;
	}
}
