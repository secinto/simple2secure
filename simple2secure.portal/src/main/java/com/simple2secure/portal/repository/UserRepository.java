package com.simple2secure.portal.repository;

import com.simple2secure.api.model.User;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class UserRepository extends MongoRepository<User> {

	public abstract User findByUsername(String username);

	public abstract User findByActivationToken(String activationToken);

	public abstract User findByEmailOnlyActivated(String email);

	public abstract User findByPasswordResetToken(String token);

	public abstract User findByEmail(String email);

	public abstract User deleteByUserID(String user_id);
}
