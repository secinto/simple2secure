package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.Processor;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class ProcessorRepository extends MongoRepository<Processor> {	
	public abstract List<Processor> getProcessorsByGroupId(String groupId);
	
	public abstract void deleteByGroupId(String groupId);

}
