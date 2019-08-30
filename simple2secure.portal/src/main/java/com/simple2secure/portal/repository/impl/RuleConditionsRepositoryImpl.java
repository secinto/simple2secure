package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.portal.repository.RuleConditionsRepository;

@Component
@Repository
@Transactional
public class RuleConditionsRepositoryImpl extends RuleConditionsRepository{
	
	@PostConstruct
	public void init() {
		super.collectionName = "templateCondition";
		super.className = TemplateCondition.class;
	}

	@Override
	public List<TemplateCondition> findTemplateConditions() {
		// TODO Auto-generated method stub
		return null;
	}

}
