package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Probe;
import com.simple2secure.api.model.User;
import com.simple2secure.commons.config.StaticConfigItems;
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
	public User findUserByUsernameAndPwd(String username, String password) {
		Query query = new Query(Criteria.where("username").is(username).and("password").is(password));
		User user = mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public User findUserByUserName(String username) {
		Query query = new Query(Criteria.where("username").is(username));
		User user = mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public int saveIfUserNotExists(User userToSave) {
		User user = null;
		Query query = new Query(Criteria.where("username").is(userToSave.getUsername()));
		user = mongoTemplate.findOne(query, User.class);

		if (user == null) {
			Query queryEmail = new Query(Criteria.where("email").is(userToSave.getEmail()));
			user = mongoTemplate.findOne(queryEmail, User.class);

			if (user == null) {
				mongoTemplate.save(userToSave);
				return StaticConfigItems.user_created;
			} else {

				return StaticConfigItems.email_exists;
			}
		} else {
			return StaticConfigItems.username_exists;
		}
	}

	@Override
	public List<Probe> findDevicesByUserID(String id) {
		User user = find(id);
		return user.getMyProbes();
	}

	@Override
	public User findByUsernameOrEmail(String username, String email) {

		User user = findUserByUserName(username);

		if (user == null) {
			user = findByEmail(email);
		}

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

	@Override
	public List<User> findByGroupId(String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		List<User> users = mongoTemplate.find(query, User.class);
		return users;
	}

	@Override
	public User findAddedByUser(String userId) {
		List<User> users = mongoTemplate.findAll(User.class);
		if (users != null) {
			for (User user : users) {
				if (user.getMyUsers() != null) {
					if (user.getMyUsers().contains(userId)) {
						return user;
					}
				}
			}
		}
		return null;
	}
}
