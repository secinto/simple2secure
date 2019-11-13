package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.WidgetUserRelation;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class WidgetUserRelRepository extends MongoRepository<WidgetUserRelation> {
	public abstract List<WidgetUserRelation> getPropertiesByUserId(String userId);
}
