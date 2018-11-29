package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Context;
import com.simple2secure.portal.repository.ContextRepository;

@Repository
@Transactional
public class ContextRepositoryImpl extends ContextRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "context"; //$NON-NLS-1$
		super.className = Context.class;
	}

	@Override
	public Context getContextByUserId(String userId) {
		List<Context> contexts = mongoTemplate.findAll(Context.class);
		if (contexts != null) {
			for (Context context : contexts) {
				if (context.getAdmins().contains(userId)) {
					return context;
				}
			}
		}
		return null;
	}

	@Override
	public Context deleteByContextId(String contextId) {
		Query query = new Query(Criteria.where("id").is(contextId));
		Context context = mongoTemplate.findOne(query, Context.class);

		if (context != null) {
			delete(context);
			return context;
		} else {
			return null;
		}
	}
}
