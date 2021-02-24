package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.CurrentContext;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.CurrentContextRepository;

@Repository
@Transactional
public class CurrentContextRepositoryImpl extends CurrentContextRepository {

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

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
	public void deleteByContextUserAuthenticationId(ObjectId contextUserAuthenticationId) {
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

	@Override
	public void deleteByContextId(ObjectId contextId) {
		List<ContextUserAuthentication> contextUserAuthList = contextUserAuthRepository.getByContextId(contextId);
		if (contextUserAuthList != null) {
			for (ContextUserAuthentication contextUserAuth : contextUserAuthList) {
				deleteByContextUserAuthenticationId(contextUserAuth.getId());
			}
		}

	}

}
