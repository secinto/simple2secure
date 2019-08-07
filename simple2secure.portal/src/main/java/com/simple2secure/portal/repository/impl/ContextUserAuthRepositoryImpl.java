package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.UserRepository;

@Repository
@Transactional
public class ContextUserAuthRepositoryImpl extends ContextUserAuthRepository {

	@Autowired
	UserRepository userRepository;

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

	@Override
	public ContextUserAuthentication getByContextIdAndUserId(String contextId, String userId) {
		Query query = new Query(Criteria.where("userId").is(userId).and("contextId").is(contextId));
		ContextUserAuthentication contextUserAuth = mongoTemplate.findOne(query, ContextUserAuthentication.class);
		if (contextUserAuth != null) {
			return contextUserAuth;
		}
		return null;
	}

	@Override
	public void deleteByContextIdAndUserId(String contextId, String userId) {
		ContextUserAuthentication contextUserAuth = getByContextIdAndUserId(contextId, userId);
		if (contextUserAuth != null) {
			mongoTemplate.remove(contextUserAuth);
		}

	}

	@Override
	public void deleteById(String id) {
		ContextUserAuthentication contextUserAuth = find(id);

		if (contextUserAuth != null) {
			mongoTemplate.remove(contextUserAuth);
		}
	}

	@Override
	public List<String> getUserIdsByUserRole(UserRole userRole) {
		List<ContextUserAuthentication> contextUserAuthList = mongoTemplate.findAll(ContextUserAuthentication.class);
		List<String> userIds = new ArrayList<>();
		if (contextUserAuthList != null) {
			for (ContextUserAuthentication contextUserAuth : contextUserAuthList) {
				if (contextUserAuth != null) {
					if (contextUserAuth.getUserRole().equals(userRole)) {
						if (!userIds.contains(contextUserAuth.getUserId())) {
							User user = userRepository.find(contextUserAuth.getUserId());
							if (user != null) {
								userIds.add(user.getId());
							}
						}
					}
				}
			}
		}
		return userIds;
	}
}
