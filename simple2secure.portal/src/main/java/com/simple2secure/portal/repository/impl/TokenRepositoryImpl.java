package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Token;
import com.simple2secure.portal.repository.TokenRepository;

@Repository
@Transactional
public class TokenRepositoryImpl extends TokenRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "token";
		super.className = Token.class;
	}

	@Override
	public Token findByUserId(String userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		return mongoTemplate.findOne(query, Token.class);
	}

	@Override
	public Token findByAccessToken(String accessToken) {
		Query query = new Query(Criteria.where("accessToken").is(accessToken));
		return mongoTemplate.findOne(query, Token.class);
	}
}
