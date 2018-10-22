package com.simple2secure.portal.repository;

import com.simple2secure.api.model.Token;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TokenRepository extends MongoRepository<Token> {
	public abstract Token findByUserId(String userId);
	public abstract Token findByProbeId(String probeId);
	public abstract Token findByAccessToken(String accessToken);
	public abstract Token findByRefreshToken(String refreshToken);
	public abstract void deleteByUserId(String userId);
}
