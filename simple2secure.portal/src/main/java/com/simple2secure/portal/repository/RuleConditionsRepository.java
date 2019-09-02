package com.simple2secure.portal.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.portal.dao.MongoRepository;

@Component
public abstract class RuleConditionsRepository extends MongoRepository<TemplateCondition>{

}
