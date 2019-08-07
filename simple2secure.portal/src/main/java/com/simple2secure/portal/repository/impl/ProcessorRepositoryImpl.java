package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Processor;
import com.simple2secure.portal.repository.ProcessorRepository;

@Repository
@Transactional
public class ProcessorRepositoryImpl extends ProcessorRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "processor"; //$NON-NLS-1$
		super.className = Processor.class;
	}
	
	@Override
	public void deleteByGroupId(String groupId) {
		List<Processor> processors = getProcessorsByGroupId(groupId);
		
		if(processors != null) {
			for(Processor processor : processors) {
				this.delete(processor);
			}			
		}
	}	

	@Override
	public List<Processor> getProcessorsByGroupId(String groupId) {
		
		Query query = new Query(Criteria.where("groupId").is(groupId));
		
		List<Processor> processors = this.mongoTemplate.find(query, Processor.class);
		return processors;
	}
}
