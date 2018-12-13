package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Tool;
import com.simple2secure.portal.repository.ToolRepository;

@Repository
@Transactional
public class ToolRepositoryImpl extends ToolRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "tool"; //$NON-NLS-1$
		super.className = Tool.class;
	}

	@Override
	public Tool getToolByName(String toolName) {
		Query query = new Query(Criteria.where("name").is(toolName));
		Tool tool = mongoTemplate.findOne(query, Tool.class);
		return tool;
	}

	@Override
	public List<Tool> getToolsByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<Tool> tools = mongoTemplate.find(query, Tool.class);
		return tools;
	}

}
