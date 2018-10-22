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
	public List<Tool> getToolsByUserID(String user_id) {
		Query query = new Query(Criteria.where("user_id").is(user_id));
		List<Tool> tools = this.mongoTemplate.find(query, Tool.class);
		return tools;
	}

	@Override
	public void deleteByUserID(String user_id) {
		List<Tool> tools = getToolsByUserID(user_id);

		for (Tool tool : tools) {
			this.mongoTemplate.remove(tool);
		}
	}

	@Override
	public Tool getToolByName(String tool_name) {
		Query query = new Query(Criteria.where("name").is(tool_name));
		Tool tool= this.mongoTemplate.findOne(query, Tool.class);
		return tool;
	}
	
	

}
