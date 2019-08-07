package com.simple2secure.portal.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.simple2secure.api.model.EmailAttribute;
import com.simple2secure.api.model.EmailAttributeEnum;
import com.simple2secure.portal.dao.MongoRepository;

@Component
public abstract class EmailAttributesListRepository extends MongoRepository<EmailAttribute> {

	public abstract List<EmailAttribute> findByAttributeType(EmailAttributeEnum type);
}
