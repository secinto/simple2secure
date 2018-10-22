package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.config.ConfigItems;
import com.simple2secure.api.model.Probe;
import com.simple2secure.api.model.User;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.UserRepository;

@Repository
@Transactional
public class UserRepositoryImpl extends UserRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "user"; //$NON-NLS-1$
		super.className = User.class;
	}

	@Override
	public User findByUserID(String id) {
		Query query = new Query(Criteria.where("id").is(id));
		User user = this.mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public User deleteByUserID(String id) {
		User user = findByUserID(id);
		this.delete(user);

		return user;
	}

	@Override
	public User findUserByUsernameAndPwd(String username, String password) {
		Query query = new Query(Criteria.where("username").is(username).and("password").is(password));
		User user = this.mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public User findUserByUserName(String username) {
		Query query = new Query(Criteria.where("username").is(username));
		User user = this.mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public int saveIfUserNotExists(User userToSave) {
		User user = null;
		Query query = new Query(Criteria.where("username").is(userToSave.getUsername()));
		user = this.mongoTemplate.findOne(query, User.class);

		if (user == null) {
			Query queryEmail = new Query(Criteria.where("email").is(userToSave.getEmail()));
			user = this.mongoTemplate.findOne(queryEmail, User.class);

			if (user == null) {
				this.mongoTemplate.save(userToSave);
				return ConfigItems.user_created;
			} else {

				return ConfigItems.email_exists;
			}
		} else {
			return ConfigItems.username_exists;
		}
	}

	@Override
	public List<Probe> findDevicesByUserID(String id) {
		User user = findByUserID(id);
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
		User user = this.mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public User findByEmailOnlyActivated(String email) {
		Query query = new Query(Criteria.where("email").is(email).andOperator(Criteria.where("activated").is(true)));
		User user = this.mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public User findByPasswordResetToken(String token) {
		Query query = new Query(Criteria.where("passwordResetToken").is(token));
		User user = this.mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public User findByEmail(String email) {
		Query query = new Query(Criteria.where("email").is(email));
		User user = this.mongoTemplate.findOne(query, User.class);
		return user;
	}

	@Override
	public List<User> findByGroupId(String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		List<User> users = this.mongoTemplate.find(query, User.class);
		return users;
	}

	@Override
	public void removeAsssignedGroup(String groupId) {
		List<User> users = findByGroupId(groupId);
		
		if(users != null) {
			for(User user : users) {
				user.setGroupId("");
				user.setGroupName("");
				try {
					this.update(user);
				} catch (ItemNotFoundRepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}

	@Override
	public User findAddedByUser(String userId) {
		List<User> users = this.mongoTemplate.findAll(User.class);
		if(users != null) {
			for(User user : users) {
				if(user.getMyUsers() != null) {
					if(user.getMyUsers().contains(userId)) {
						return user;
					}
				}
			}
		}
		return null;
	}
}
