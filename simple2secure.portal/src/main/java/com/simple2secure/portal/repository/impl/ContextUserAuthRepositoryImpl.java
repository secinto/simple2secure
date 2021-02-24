package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
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
	public List<ContextUserAuthentication> getByContextId(ObjectId contextId) {
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
	public void deleteByContextId(ObjectId contextId) {
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

	@Override
	public ContextUserAuthentication getByContextIdAndUserId(ObjectId contextId, String userId) {
		Query query = new Query(Criteria.where("userId").is(userId).and("contextId").is(contextId));
		ContextUserAuthentication contextUserAuth = mongoTemplate.findOne(query, ContextUserAuthentication.class);
		if (contextUserAuth != null) {
			return contextUserAuth;
		}
		return null;
	}

	@Override
	public void deleteByContextIdAndUserId(ObjectId contextId, String userId) {
		ContextUserAuthentication contextUserAuth = getByContextIdAndUserId(contextId, userId);
		if (contextUserAuth != null) {
			mongoTemplate.remove(contextUserAuth);
		}

	}

	@Override
	public void deleteById(ObjectId id) {
		ContextUserAuthentication contextUserAuth = find(id);

		if (contextUserAuth != null) {
			mongoTemplate.remove(contextUserAuth);
		}
	}

	@Override
	public List<ContextUserAuthentication> getAdminsByUserId(String userId) {
		Query query = new Query(Criteria.where("userId").is(userId).and("userRole").is("ADMIN"));
		List<ContextUserAuthentication> contextUserAuths = mongoTemplate.find(query, ContextUserAuthentication.class);
		return contextUserAuths;
	}
}
