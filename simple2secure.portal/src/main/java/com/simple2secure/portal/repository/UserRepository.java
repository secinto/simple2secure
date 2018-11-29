package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Probe;
import com.simple2secure.api.model.User;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class UserRepository extends MongoRepository<User> {
	public abstract User deleteByUserID(String user_id);

	public abstract User findUserByUsernameAndPwd(String username, String password);

	public abstract User findUserByUserName(String username);

	public abstract int saveIfUserNotExists(User userToSave);

	public abstract List<Probe> findDevicesByUserID(String userId);

	public abstract User findByUsernameOrEmail(String username, String email);

	public abstract User findByActivationToken(String activationToken);

	public abstract User findByEmailOnlyActivated(String email);

	public abstract User findByPasswordResetToken(String token);

	public abstract User findByEmail(String email);

	public abstract List<User> findByGroupId(String groupId);

	public abstract User findAddedByUser(String userId);

	public abstract List<Context> findAssignedContextsByUserId(String userId);
}
