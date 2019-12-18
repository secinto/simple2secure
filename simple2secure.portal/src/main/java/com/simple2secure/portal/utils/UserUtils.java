/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
package com.simple2secure.portal.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.UserRoleDTO;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.GroupAccessRight;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserInfo;
import com.simple2secure.api.model.UserInvitation;
import com.simple2secure.api.model.UserRegistration;
import com.simple2secure.api.model.UserRegistrationType;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.providers.BaseServiceProvider;
import com.simple2secure.portal.validation.model.ValidInputLocale;

@SuppressWarnings("unchecked")
@Component
public class UserUtils extends BaseServiceProvider {

	private static Logger log = LoggerFactory.getLogger(UserUtils.class);

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	MailUtils mailUtils;

	@Autowired
	GroupUtils groupUtils;

	@Autowired
	ContextUtils contextUtils;

	/**
	 * This function checks if user already exists and calls the inviteUserToContext in case if the registration type is ADDED_BY_USER, in all
	 * other cases an error will be returned. If user does not exist, according to the registration type the correct function will be called:
	 * addNewUserAddedByRegistration or addUserStandardRegistration
	 *
	 * @param userRegistration
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 */

	public ResponseEntity<User> addNewUser(UserRegistration userRegistration, ValidInputLocale locale)
			throws ItemNotFoundRepositoryException, IOException {
		if (userRegistration != null) {
			if (!Strings.isNullOrEmpty(userRegistration.getEmail())) {

				// If user exists we can call inviteUserToContext or return an error in case of standard registration
				if (checkIfUserExists(userRegistration.getEmail())) {
					// If the user is added by another user call inviteUserToContext function
					if (userRegistration.getRegistrationType().equals(UserRegistrationType.ADDED_BY_USER)) {
						return inviteUserToContext(userRegistration, locale);
					}
					// In case of standard registration return an error
					else {
						return ((ResponseEntity<User>) buildResponseEntity("user_with_provided_email_already_exists", locale));
					}
				} else {
					if (userRegistration.getRegistrationType().equals(UserRegistrationType.ADDED_BY_USER)) {
						return addUserAddedByRegistration(userRegistration, locale);
					} else if (userRegistration.getRegistrationType().equals(UserRegistrationType.STANDARD)) {
						return addUserStandardRegistration(userRegistration, locale);
					} else if (userRegistration.getRegistrationType().equals(UserRegistrationType.INITIALIZATION)) {
						return initializeSecintoUsers(userRegistration, locale);
					}
				}
			}
		}
		return ((ResponseEntity<User>) buildResponseEntity("unknown_error_occured", locale));
	}

	/**
	 * This function is used to add user in case that we add the user with type AddedByUser.
	 *
	 * @param userRegistration
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 */
	public ResponseEntity<User> addUserAddedByRegistration(UserRegistration userRegistration, ValidInputLocale locale)
			throws ItemNotFoundRepositoryException, IOException {

		if (!Strings.isNullOrEmpty(userRegistration.getAddedByUserId())) {
			User addedByUser = userRepository.find(userRegistration.getAddedByUserId());

			// if addedByUser not null continue
			if (addedByUser != null) {
				if (!Strings.isNullOrEmpty(userRegistration.getEmail()) && userRegistration.getUserRole() != null) {
					User user = new User(userRegistration.getEmail());
					user.setEnabled(true);
					user.setActivationToken(portalUtils.generateToken());
					user.setActivated(false);

					// Get context by current working context id
					Context context = contextRepository.find(userRegistration.getCurrentContextId());

					// If current working context is not null save user and return userId
					if (context != null) {
						ObjectId userID = userRepository.saveAndReturnId(user);

						// Map current context to the current user
						ObjectId contextUserId = contextUtils.addContextUserAuthentication(userID.toString(), context.getId(),
								userRegistration.getUserRole(), false);

						// If sending email was successful, update addedByUser, if not delete the user and userContextAuth
						if (mailUtils.sendEmail(user, mailUtils.generateEmailContent(user, locale.getValue()), StaticConfigItems.email_subject_al)
								&& contextUserId != null) {

							// Add userInfo to the current user
							addUserInfo(userID.toString(), user.getEmail());

							// Update the selected groups to be accessible for the superuser
							if (userRegistration.getUserRole().equals(UserRole.SUPERUSER)) {

								groupUtils.updateGroupAccessRightsforTheSuperuser(userRegistration.getGroupIds(), user, context);

							}

							log.debug("User {} added successfully", user.getEmail());
							return new ResponseEntity<>(user, HttpStatus.OK);
						} else {
							userRepository.deleteByUserID(userID.toString());

							if (contextUserId != null) {
								contextUserAuthRepository.deleteById(contextUserId.toString());
							}

							log.error("Error while sending activation email, user {} has been deleted", user.getEmail());

							return ((ResponseEntity<User>) buildResponseEntity("error_while_sending_email", locale));
						}
					}
				}
			}
		}
		return ((ResponseEntity<User>) buildResponseEntity("unknown_error_occured", locale));
	}

	/**
	 * This function is used for the standard registration.
	 *
	 * @param userRegistration
	 * @param locale
	 * @return
	 * @throws IOException
	 */
	public ResponseEntity<User> addUserStandardRegistration(UserRegistration userRegistration, ValidInputLocale locale) throws IOException {
		if (!Strings.isNullOrEmpty(userRegistration.getEmail()) && !Strings.isNullOrEmpty(userRegistration.getPassword())) {

			User user = new User(userRegistration.getEmail());
			user.setActivationToken(portalUtils.generateToken());
			user.setEnabled(true);
			user.setActivated(false);
			user.setPasswordUpdated(true);
			user.setPassword(userRegistration.getPassword());

			// validate the user password
			String error = validateUserPassword(user);
			if (!Strings.isNullOrEmpty(error)) {
				return ((ResponseEntity<User>) buildResponseEntity(error, locale));
			}

			user.setPassword(passwordEncoder.encode(user.getPassword()));

			// Return userId and get user with id from this id
			ObjectId userID = userRepository.saveAndReturnId(user);

			ObjectId contextId = contextUtils.addNewContextForRegistration(user, userID);

			if (contextId != null) {
				if (mailUtils.sendEmail(user, mailUtils.generateEmailContent(user, locale.getValue()), StaticConfigItems.email_subject_al)) {

					// add user info to the current user
					addUserInfo(userID.toString(), user.getEmail());

					// add standard group for current user
					dataInitialization.addDefaultGroup(userID.toString(), contextId.toString());
					// Map current context with the current user
					contextUtils.addContextUserAuthentication(userID.toString(), contextId.toString(), UserRole.ADMIN, true);

					// Map all SUPERADMINs to this context
					contextUtils.mapSuperAdminsTotheContext(contextId.toString());
					return new ResponseEntity<>(user, HttpStatus.OK);
				} else {

					userRepository.deleteByUserID(userID.toString());
					contextRepository.deleteByContextId(contextId.toString());

					log.error("Error while sending activation email, user {} has been deleted", user.getEmail());
					return ((ResponseEntity<User>) buildResponseEntity("unknown_error_occured", locale));
				}

			} else {
				userRepository.deleteByUserID(userID.toString());
				return ((ResponseEntity<User>) buildResponseEntity("unknown_error_occured", locale));
			}

		}
		return ((ResponseEntity<User>) buildResponseEntity("unknown_error_occured", locale));
	}

	/**
	 * This function checks if user already exists and calls the inviteUserToContext in case if the registration type is ADDED_BY_USER, in all
	 * other cases an error will be returned. If user does not exist, according to the registration type the correct function will be called:
	 * addNewUserAddedByRegistration or addUserStandardRegistration
	 *
	 * @param userRegistration
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 */
	public ResponseEntity<User> resendActivation(User user, ValidInputLocale locale) throws ItemNotFoundRepositoryException, IOException {
		// Return userId and get user with id from this id
		if (mailUtils.sendEmail(user, mailUtils.generateEmailContent(user, locale.getValue()), StaticConfigItems.email_subject_al)) {
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
		return ((ResponseEntity<User>) buildResponseEntity("unknown_error_occured", locale));
	}

	/**
	 * This function is used to initialize secinto users if those are not already added. For the addition users, add an email address to the
	 * SECINTO_EMAIL_LIST array in the StaticConfigItems.
	 *
	 * @param userRegistration
	 * @param locale
	 * @return
	 * @throws IOException
	 */
	public ResponseEntity<User> initializeSecintoUsers(UserRegistration userRegistration, ValidInputLocale locale) throws IOException {
		if (!Strings.isNullOrEmpty(userRegistration.getEmail())) {

			User user = new User(userRegistration.getEmail());
			user.setActivationToken(portalUtils.generateToken());
			user.setEnabled(true);
			user.setActivated(false);

			// Return userId and get user with id from this id
			ObjectId userID = userRepository.saveAndReturnId(user);

			ObjectId contextId = contextUtils.addNewContextForRegistration(user, userID);

			if (contextId != null) {
				if (mailUtils.sendEmail(user, mailUtils.generateEmailContent(user, locale.getValue()), StaticConfigItems.email_subject_al)) {
					// add user info for current user
					addUserInfo(userID.toString(), user.getEmail());

					// add standard group for current user
					dataInitialization.addDefaultGroup(userID.toString(), contextId.toString());

					// Map current context with the current user
					contextUtils.addContextUserAuthentication(userID.toString(), contextId.toString(), UserRole.SUPERADMIN, true);

					// Map all SUPERADMINs to this context
					contextUtils.mapSuperAdminsTotheContext(contextId.toString());

					return new ResponseEntity<>(user, HttpStatus.OK);
				} else {

					userRepository.deleteByUserID(userID.toString());
					contextRepository.deleteByContextId(contextId.toString());

					log.error("Error while sending activation email, user {} has been deleted", user.getEmail());
					return ((ResponseEntity<User>) buildResponseEntity("unknown_error_occured", locale));
				}

			} else {
				userRepository.deleteByUserID(userID.toString());
				return ((ResponseEntity<User>) buildResponseEntity("unknown_error_occured", locale));
			}

		}
		return ((ResponseEntity<User>) buildResponseEntity("unknown_error_occured", locale));
	}

	/**
	 * This function validates the user password and returns null in case that password is ok. In all other cases an error will be returned
	 * and the procedure will be stopped.
	 *
	 * @param user
	 * @param locale
	 * @return
	 */
	public String validateUserPassword(User user) {
		Errors errors = new BeanPropertyBindingResult(user, "user");

		passwordValidator.validate(user, errors);

		if (errors.hasErrors()) {
			return errors.getAllErrors().get(0).getDefaultMessage();
		}
		return null;
	}

	public ResponseEntity<User> updateUser(UserRegistration userRegistration, ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (userRegistration != null && !Strings.isNullOrEmpty(userRegistration.getEmail())
				&& !Strings.isNullOrEmpty(userRegistration.getCurrentContextId())) {
			User user = userRepository.findByEmail(userRegistration.getEmail());
			Context context = contextRepository.find(userRegistration.getCurrentContextId());
			if (user != null && context != null) {

				if (checkIfUserCanUpdateUser(userRegistration, context)) {
					// get current context user auth and check if user role has been changed
					ContextUserAuthentication contextUserAuth = contextUserAuthRepository.getByContextIdAndUserId(context.getId(), user.getId());

					if (contextUserAuth != null) {
						if (!userRegistration.getUserRole().equals(contextUserAuth.getUserRole())) {

							if (contextUserAuth.getUserRole().equals(UserRole.SUPERUSER)) {
								groupUtils.removeSuperuserFromtheGroupAccessRights(user, context);
							}

							if (userRegistration.getUserRole().equals(UserRole.SUPERUSER)) {
								groupUtils.updateGroupAccessRightsforTheSuperuser(userRegistration.getGroupIds(), user, context);
							}

							// update contextUserAuthentication object with the current role
							contextUserAuth.setUserRole(userRegistration.getUserRole());
							contextUserAuthRepository.update(contextUserAuth);
						} else {
							if (userRegistration.getUserRole().equals(UserRole.SUPERUSER)) {
								groupUtils.removeSuperuserFromtheGroupAccessRights(user, context);
								groupUtils.updateGroupAccessRightsforTheSuperuser(userRegistration.getGroupIds(), user, context);
							}
						}
						return new ResponseEntity<>(user, HttpStatus.OK);
					}
				}
			}
		}
		return ((ResponseEntity<User>) buildResponseEntity("unknown_error_occured", locale));
	}

	/**
	 * This function checks if user already exists according to the user email
	 *
	 * @param email
	 * @return
	 */
	private boolean checkIfUserExists(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			return false;
		}
		return true;
	}

	/**
	 * This function is used to invite the user to the current context which is included in the UserRegistration object
	 *
	 * @param userRegistration
	 * @throws IOException
	 */
	private ResponseEntity<User> inviteUserToContext(UserRegistration userRegistration, ValidInputLocale locale) throws IOException {

		// invite is only possible if user is added by another user
		if (userRegistration.getRegistrationType().equals(UserRegistrationType.ADDED_BY_USER)) {
			if (!Strings.isNullOrEmpty(userRegistration.getAddedByUserId())) {

				User addedByUser = userRepository.find(userRegistration.getAddedByUserId());
				Context context = contextRepository.find(userRegistration.getCurrentContextId());
				User user = userRepository.findByEmailOnlyActivated(userRegistration.getEmail());
				if (addedByUser != null && context != null && user != null) {
					// Check if user is already part of this context
					ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(context.getId(),
							user.getId());
					// user not part of this context
					if (contextUserAuthentication == null) {

						// TODO: obtain expiration_time_password_reset value from settings
						long tokenExpirationTime = System.currentTimeMillis() + StaticConfigItems.expiration_time_password_reset;

						// create new invitation
						UserInvitation userInvitation = new UserInvitation(user.getId(), context.getId(), userRegistration.getUserRole(),
								portalUtils.generateToken(), tokenExpirationTime, userRegistration.getGroupIds());

						// If email has been sent save the user invitation to the database
						if (mailUtils.sendEmail(user, mailUtils.generateInvitationEmail(userInvitation, context, addedByUser, locale.getValue()),
								StaticConfigItems.email_subject_inv)) {
							userInvitationRepository.save(userInvitation);
							return new ResponseEntity<>(user, HttpStatus.OK);
						}

					}
				}
			}

		}
		return ((ResponseEntity<User>) buildResponseEntity("user_with_provided_email_already_exists", locale));

	}

	/**
	 * This function returns all users from the current context except the current user which requested this action
	 *
	 * @param context
	 * @return
	 */
	public List<UserRoleDTO> getAllUsersFromCurrentContext(Context context, String userId) {
		List<UserRoleDTO> myUsersWithRole = new ArrayList<>();
		List<ContextUserAuthentication> contextUserAuthList = contextUserAuthRepository.getByContextId(context.getId());

		if (contextUserAuthList != null) {
			for (ContextUserAuthentication contextUserAuth : contextUserAuthList) {
				if (contextUserAuth != null) {
					if (!contextUserAuth.getUserId().equals(userId)) {
						User user = userRepository.find(contextUserAuth.getUserId());
						if (user != null) {
							UserInfo userInfo = userInfoRepository.getByUserId(user.getId());
							if (userInfo != null) {
								List<String> groupIds = new ArrayList<>();

								if (contextUserAuth.getUserRole().equals(UserRole.SUPERUSER)) {
									List<GroupAccessRight> accessRightList = groupAccessRightRepository.findByContextIdAndUserId(context.getId(),
											user.getId());

									for (GroupAccessRight accessRight : accessRightList) {
										if (accessRight != null) {
											if (groupUtils.checkIfGroupExists(accessRight.getGroupId())) {
												groupIds.add(accessRight.getGroupId());
											}
										}
									}
								}
								myUsersWithRole.add(new UserRoleDTO(userInfo, contextUserAuth.getUserRole(), groupIds));
							}
						}
					}
				}
			}
		}

		return myUsersWithRole;
	}

	/**
	 * This function checks if currently active user has privileges to update selected user
	 *
	 * @param userRegistration
	 * @param context
	 * @return
	 */
	private boolean checkIfUserCanUpdateUser(UserRegistration userRegistration, Context context) {
		if (!Strings.isNullOrEmpty(userRegistration.getAddedByUserId())) {
			User user = userRepository.find(userRegistration.getAddedByUserId());
			if (user != null) {
				ContextUserAuthentication contextUserAuth = contextUserAuthRepository.getByContextIdAndUserId(context.getId(), user.getId());
				if (contextUserAuth != null) {
					if (contextUserAuth.getUserRole().equals(UserRole.SUPERADMIN)) {
						return true;
					} else if (contextUserAuth.getUserRole().equals(UserRole.ADMIN)) {
						if (!userRegistration.getUserRole().equals(UserRole.SUPERADMIN)) {
							return true;
						}
					} else if (contextUserAuth.getUserRole().equals(UserRole.SUPERUSER)) {
						if (!userRegistration.getUserRole().equals(UserRole.SUPERADMIN) && !userRegistration.getUserRole().equals(UserRole.ADMIN)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * This function adds the userInfo for the provided user
	 *
	 * @param userId
	 * @param email
	 */
	private void addUserInfo(String userId, String email) {
		UserInfo userInfo = new UserInfo(userId, email);
		userInfoRepository.save(userInfo);
	}
}
