package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Step;
import com.simple2secure.portal.repository.StepRepository;

@Repository
@Transactional
public class StepRepositoryImpl extends StepRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "step"; //$NON-NLS-1$
		super.className = Step.class;
	}

	@Override
	public Step getByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));

		Step step = mongoTemplate.findOne(query, Step.class);

		return step;
	}

	@Override
	public List<Step> getAllGreaterThanNumber(int stepNumber) {
		Query query = new Query(Criteria.where("number").gt(stepNumber));
		List<Step> steps = mongoTemplate.find(query, Step.class);
		return steps;
	}

	@Override
	public List<Step> getStepsByFlagValue(boolean select_all) {
		List<Step> steps = new ArrayList<>();
		if (select_all) {
			steps = mongoTemplate.findAll(className);
		} else {
			Query query = new Query(Criteria.where("active").is(1));
			steps = mongoTemplate.find(query, className);
		}

		return steps;
	}
}
