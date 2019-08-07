package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.Tool;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class ToolRepository extends MongoRepository<Tool> {

	public abstract List<Tool> getToolsByUserID(String user_id);

	public abstract void deleteByUserID(String user_id);
	
	public abstract Tool getToolByName(String tool_name);
}
