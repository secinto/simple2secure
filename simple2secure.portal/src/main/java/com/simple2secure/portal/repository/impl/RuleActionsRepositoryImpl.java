package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TemplateAction;
import com.simple2secure.portal.repository.RuleActionsRepository;

@Component
@Repository
@Transactional
public class RuleActionsRepositoryImpl extends RuleActionsRepository{

	@PostConstruct
	public void init() {
		super.collectionName = "templateAction";
		super.className = TemplateAction.class;
	}
	
	
	@Override
	public List<TemplateAction> findTemplateActions() {
		// TODO Auto-generated method stub
		return null;
	}
}
