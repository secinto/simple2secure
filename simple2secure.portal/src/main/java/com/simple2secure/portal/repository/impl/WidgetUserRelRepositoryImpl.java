package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.WidgetUserRelation;
import com.simple2secure.portal.repository.WidgetUserRelRepository;

@Repository
@Transactional
public class WidgetUserRelRepositoryImpl extends WidgetUserRelRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "widgetUserRelation"; //$NON-NLS-1$
		super.className = WidgetUserRelation.class;
	}

	@Override
	public List<WidgetUserRelation> getPropertiesByUserId(String userId) {
		Query query = new Query(Criteria.where("userId").is(userId));

		List<WidgetUserRelation> widgetUserRelations = mongoTemplate.find(query, className, collectionName);

		return widgetUserRelations;
	}
}
