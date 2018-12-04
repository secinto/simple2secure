package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.CurrentContext;
import com.simple2secure.portal.repository.CurrentContextRepository;

@Repository
@Transactional
public class CurrentContextRepositoryImpl extends CurrentContextRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "currentContext"; //$NON-NLS-1$
		super.className = CurrentContext.class;
	}

	@Override
	public CurrentContext findByUserId(String userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		CurrentContext currentContext = mongoTemplate.findOne(query, CurrentContext.class);
		return currentContext;
	}

	@Override
	public void deleteByContextUserAuthenticationId(String contextUserAuthenticationId) {
		Query query = new Query(Criteria.where("contextUserAuthenticationId").is(contextUserAuthenticationId));
		List<CurrentContext> currentContextList = mongoTemplate.find(query, CurrentContext.class);
		if (currentContextList != null) {
			for (CurrentContext currentContext : currentContextList) {
				if (currentContext != null) {
					delete(currentContext);
				}
			}
		}
	}

}
