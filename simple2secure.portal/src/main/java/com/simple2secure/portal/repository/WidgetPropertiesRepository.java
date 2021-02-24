package com.simple2secure.portal.repository;

import java.util.List;

import org.bson.types.ObjectId;

import com.simple2secure.api.model.WidgetProperties;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class WidgetPropertiesRepository extends MongoRepository<WidgetProperties> {

	public abstract List<WidgetProperties> getPropertiesByUserIdAndContextIdAndLocation(String userId, ObjectId contextId, String location);

}
