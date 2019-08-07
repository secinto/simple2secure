package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.EmailList;
import com.simple2secure.api.model.EmailListEnum;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class EmailListRepository extends MongoRepository<EmailList> {

	public abstract List<EmailList> findByEmailType(EmailListEnum type);
}
