package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.User;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.UserRepository;

@Repository
@Transactional
public class UserRepositoryImpl extends UserRepository {

	@Autowired
	ContextRepository contextRepository;

	@PostConstruct
	public void init() {
		super.collectionName = "user"; //$NON-NLS-1$
		super.className = User.class;
	}

	@Override
	public User deleteByUserID(String id) {
		User user = find(id);
		this.delete(user);

		return user;
	}

	@Override
	public User findByActivationToken(String activationToken) {
		Query query = new Query(Criteria.where("activationToken").is(activationToken));
		User user = mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public User findByEmailOnlyActivated(String email) {
		Query query = new Query(Criteria.where("email").is(email).andOperator(Criteria.where("activated").is(true)));
		User user = mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public User findByPasswordResetToken(String token) {
		Query query = new Query(Criteria.where("passwordResetToken").is(token));
		User user = mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public User findByEmail(String email) {
		Query query = new Query(Criteria.where("email").is(email));
		User user = mongoTemplate.findOne(query, User.class);
		return user;
	}
}
