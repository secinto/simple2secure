package com.simple2secure.portal.repository.impl;

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
	public void deleteByGroupId(String groupId) {
		List<Step> steps = getStepsByGroupId(groupId, true);
		if(steps != null) {
			for(Step step : steps) {
				this.delete(step);
			}			
		}		
	}
	

	@Override
	public List<Step> getStepsByGroupId(String groupId, boolean select_all) {
		Query query = new Query();

		if (select_all) {
			query = new Query(Criteria.where("groupId").is(groupId));
			
		} else {
			query = new Query(Criteria.where("groupId").is(groupId).and("active").is(1));
			
		}
		List<Step> steps = this.mongoTemplate.find(query, Step.class);
		return steps;
	}

	@Override
	public Step getByNameAndGroupId(String name, String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId).and("name").is(name));
		
		Step step = this.mongoTemplate.findOne(query, Step.class);
		
		return step;
	}	
}
