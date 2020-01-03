package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.WidgetProperties;
import com.simple2secure.portal.repository.WidgetPropertiesRepository;

@Repository
@Transactional
public class WidgetPropertiesRepositoryImpl extends WidgetPropertiesRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "widgetProperties"; //$NON-NLS-1$
		super.className = WidgetProperties.class;
	}

	@Override
	public List<WidgetProperties> getPropertiesByUserIdAndContextId(String userId, String contextId) {
		Query query = new Query(Criteria.where("userId").is(userId).and("contextId").is(contextId));

		List<WidgetProperties> widgetProperties = mongoTemplate.find(query, className, collectionName);

		return widgetProperties;
	}
}
