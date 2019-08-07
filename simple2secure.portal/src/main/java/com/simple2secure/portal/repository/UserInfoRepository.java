package com.simple2secure.portal.repository;

import com.simple2secure.api.model.UserInfo;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class UserInfoRepository extends MongoRepository<UserInfo> {

	public abstract UserInfo getByUserId(String userId);

	public abstract void deleteByUserId(String userId);
}
