package com.simple2secure.portal.repository;

import com.simple2secure.api.model.ServiceLibrary;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class ServiceLibraryRepository extends MongoRepository<ServiceLibrary> {
	public abstract ServiceLibrary findByVersion(String version);
}
