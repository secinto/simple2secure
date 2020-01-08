package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.WidgetProperties;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class WidgetPropertiesRepository extends MongoRepository<WidgetProperties> {

	public abstract List<WidgetProperties> getPropertiesByUserIdAndContextIdAndLocation(String userId, String contextId, String location);

}
