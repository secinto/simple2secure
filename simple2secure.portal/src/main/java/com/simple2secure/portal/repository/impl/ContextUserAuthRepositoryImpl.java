package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.portal.repository.ContextUserAuthRepository;

@Repository
@Transactional
public class ContextUserAuthRepositoryImpl extends ContextUserAuthRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "contextUserAuthentication"; //$NON-NLS-1$
		super.className = ContextUserAuthentication.class;
	}

	@Override
	public List<ContextUserAuthentication> getByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<ContextUserAuthentication> contextUserAuths = mongoTemplate.find(query, ContextUserAuthentication.class);
		return contextUserAuths;
	}

	@Override
	public List<ContextUserAuthentication> getByUserId(String userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		List<ContextUserAuthentication> contextUserAuths = mongoTemplate.find(query, ContextUserAuthentication.class);
		return contextUserAuths;
	}

	@Override
	public void deleteByContextId(String contextId) {
		List<ContextUserAuthentication> contextUserAuths = getByContextId(contextId);

		if (contextUserAuths != null) {
			for (ContextUserAuthentication contextUserAuth : contextUserAuths) {
				mongoTemplate.remove(contextUserAuth);
			}
		}

	}

	@Override
	public void deleteByUserId(String userId) {
		List<ContextUserAuthentication> contextUserAuths = getByUserId(userId);

		if (contextUserAuths != null) {
			for (ContextUserAuthentication contextUserAuth : contextUserAuths) {
				mongoTemplate.remove(contextUserAuth);
			}
		}
	}

}
