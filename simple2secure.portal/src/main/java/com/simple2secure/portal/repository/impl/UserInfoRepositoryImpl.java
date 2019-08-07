package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.UserInfo;
import com.simple2secure.portal.repository.UserInfoRepository;

@Repository
@Transactional
public class UserInfoRepositoryImpl extends UserInfoRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "userInfo"; //$NON-NLS-1$
		super.className = UserInfo.class;
	}

	@Override
	public UserInfo getByUserId(String userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		UserInfo userInfo = mongoTemplate.findOne(query, UserInfo.class);
		return userInfo;
	}

	@Override
	public void deleteByUserId(String userId) {
		UserInfo userInfo = getByUserId(userId);
		if (userInfo != null) {
			delete(userInfo);
		}
	}

}
