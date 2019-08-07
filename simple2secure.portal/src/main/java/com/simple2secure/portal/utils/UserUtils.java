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
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.EmailRepository;
import com.simple2secure.portal.repository.GroupAccesRightRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.repository.RuleRepository;
import com.simple2secure.portal.repository.TokenRepository;
import com.simple2secure.portal.repository.UserInfoRepository;
import com.simple2secure.portal.repository.UserInvitationRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.security.PasswordValidator;
import com.simple2secure.portal.service.MessageByLocaleService;

@Component
public class UserUtils {

	private static Logger log = LoggerFactory.getLogger(UserUtils.class);

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	MailUtils mailUtils;

	@Autowired
	GroupUtils groupUtils;

	@Autowired
	ContextUtils contextUtils;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	EmailConfigurationRepository emailConfigRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	LicensePlanRepository licensePlanRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	RuleRepository ruleRepository;

	@Autowired
	EmailRepository emailRepository;

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	GroupAccesRightRepository groupAccessRightRepository;

	@Autowired
	UserInvitationRepository userInvitationRepository;

	@Autowired
	UserInfoRepository userInfoRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	PasswordValidator passwordValidator;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	DataInitialization dataInitialization;

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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<User> addNewUser(UserRegistration userRegistration, String locale)
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
						return new ResponseEntity(
								new CustomErrorType(messageByLocaleService.getMessage("user_with_provided_email_already_exists", locale)),
								HttpStatus.NOT_FOUND);
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

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function is used to add user in case that we user the type AddedByUser.
	 *
	 * @param userRegistration
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity<User> addUserAddedByRegistration(UserRegistration userRegistration, String locale)
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
						if (mailUtils.sendEmail(user, mailUtils.generateEmailContent(user, locale), StaticConfigItems.email_subject_al)
								&& contextUserId != null) {

							// Add userInfo to the current user
							addUserInfo(userID.toString(), user.getEmail());

							// Update the selected groups to be accessible for the superuser
							if (userRegistration.getUserRole().equals(UserRole.SUPERUSER)) {

								groupUtils.updateGroupAccessRightsforTheSuperuser(userRegistration.getGroupIds(), user, context);

							}

							log.debug("User {} added successfully", user.getEmail());
							return new ResponseEntity<User>(user, HttpStatus.OK);
						} else {
							userRepository.deleteByUserID(userID.toString());
							contextUserAuthRepository.deleteById(contextUserId.toString());

							log.error("Error while sending activation email, user {} has been deleted", user.getEmail());

							return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_sending_email", locale)),
									HttpStatus.NOT_FOUND);
						}
					}
				}
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function is used for the standard registration.
	 *
	 * @param userRegistration
	 * @param locale
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<User> addUserStandardRegistration(UserRegistration userRegistration, String locale) throws IOException {
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
				return new ResponseEntity(new CustomErrorType(error), HttpStatus.NOT_FOUND);
			}

			user.setPassword(passwordEncoder.encode(user.getPassword()));

			// Return userId and get user with id from this id
			ObjectId userID = userRepository.saveAndReturnId(user);

			ObjectId contextId = contextUtils.addNewContextForRegistration(user, userID);

			if (contextId != null) {
				if (mailUtils.sendEmail(user, mailUtils.generateEmailContent(user, locale), StaticConfigItems.email_subject_al)) {

					// add user info to the current user
					addUserInfo(userID.toString(), user.getEmail());

					// add standard group for current user
					dataInitialization.addDefaultGroup(userID.toString(), contextId.toString());
					// Map current context with the current user
					contextUtils.addContextUserAuthentication(userID.toString(), contextId.toString(), UserRole.ADMIN, true);

					// Map all SUPERADMINs to this context
					contextUtils.mapSuperAdminsTotheContext(contextId.toString());
					return new ResponseEntity<User>(user, HttpStatus.OK);
				} else {

					userRepository.deleteByUserID(userID.toString());
					contextRepository.deleteByContextId(contextId.toString());

					log.error("Error while sending activation email, user {} has been deleted", user.getEmail());

					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
							HttpStatus.NOT_FOUND);
				}

			} else {
				userRepository.deleteByUserID(userID.toString());
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
						HttpStatus.NOT_FOUND);
			}

		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
				HttpStatus.NOT_FOUND);

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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<User> initializeSecintoUsers(UserRegistration userRegistration, String locale) throws IOException {
		if (!Strings.isNullOrEmpty(userRegistration.getEmail())) {

			User user = new User(userRegistration.getEmail());
			user.setActivationToken(portalUtils.generateToken());
			user.setEnabled(true);
			user.setActivated(false);

			// Return userId and get user with id from this id
			ObjectId userID = userRepository.saveAndReturnId(user);

			ObjectId contextId = contextUtils.addNewContextForRegistration(user, userID);

			if (contextId != null) {
				if (mailUtils.sendEmail(user, mailUtils.generateEmailContent(user, locale), StaticConfigItems.email_subject_al)) {
					// add user info for current user
					addUserInfo(userID.toString(), user.getEmail());

					// add standard group for current user
					dataInitialization.addDefaultGroup(userID.toString(), contextId.toString());

					// Map current context with the current user
					contextUtils.addContextUserAuthentication(userID.toString(), contextId.toString(), UserRole.SUPERADMIN, true);

					// Map all SUPERADMINs to this context
					contextUtils.mapSuperAdminsTotheContext(contextId.toString());

					return new ResponseEntity<User>(user, HttpStatus.OK);
				} else {

					userRepository.deleteByUserID(userID.toString());
					contextRepository.deleteByContextId(contextId.toString());

					log.error("Error while sending activation email, user {} has been deleted", user.getEmail());

					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
							HttpStatus.NOT_FOUND);
				}

			} else {
				userRepository.deleteByUserID(userID.toString());
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
						HttpStatus.NOT_FOUND);
			}

		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
				HttpStatus.NOT_FOUND);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<User> updateUser(UserRegistration userRegistration, String locale) throws ItemNotFoundRepositoryException {

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
						return new ResponseEntity<User>(user, HttpStatus.OK);
					}
				}
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
				HttpStatus.NOT_FOUND);
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ResponseEntity<User> inviteUserToContext(UserRegistration userRegistration, String locale) throws IOException {

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
						if (mailUtils.sendEmail(user, mailUtils.generateInvitationEmail(userInvitation, context, addedByUser, locale),
								StaticConfigItems.email_subject_inv)) {
							userInvitationRepository.save(userInvitation);
							return new ResponseEntity<User>(user, HttpStatus.OK);
						}

					}
				}
			}

		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("user_with_provided_email_already_exists", locale)),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function returns all users from the current context except the current user which requested this action
	 *
	 * @param context
	 * @return
	 */
	public List<UserRoleDTO> getAllUsersFromCurrentContext(Context context, String userId) {
		List<UserRoleDTO> myUsersWithRole = new ArrayList<UserRoleDTO>();
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
