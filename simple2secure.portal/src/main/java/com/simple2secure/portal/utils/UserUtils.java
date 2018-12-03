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
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.User;
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
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.repository.RuleRepository;
import com.simple2secure.portal.repository.TokenRepository;
import com.simple2secure.portal.repository.ToolRepository;
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
	UserRepository userRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	ToolRepository toolRepository;

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
					User user = new User(userRegistration.getEmail(), userRegistration.getUserRole());
					user.setUsername(userRegistration.getEmail());
					user.setEnabled(true);
					user.setActivationToken(portalUtils.generateToken());
					user.setActivated(false);

					// Get context by current working context id
					Context context = contextRepository.find(userRegistration.getCurrentContextId());

					// If current working context is not null save user and return userId
					if (context != null) {
						ObjectId userID = userRepository.saveAndReturnId(user);

						// Map current context to the current user
						ObjectId contextUserId = addContextUserAuthentication(userID.toString(), context.getId(), userRegistration.getUserRole());

						// Add this user to the addedByUser List
						addedByUser.addMyUser(userID.toString());

						// If sending email was successful, update addedByUser, if not delete the user and userContextAuth
						if (mailUtils.sendEmail(user, mailUtils.generateEmailContent(user, locale), StaticConfigItems.email_subject_al)
								&& contextUserId != null) {
							userRepository.update(addedByUser);

							// Update the selected groups to be accessible for the superuser
							if (userRegistration.getUserRole().equals(UserRole.SUPERUSER)) {
								addSuperuserTotheSelectedGroup(userRegistration, userID.toString());
							}

							log.debug("User {} added successfully", user.getUsername());
							return new ResponseEntity<User>(user, HttpStatus.OK);
						} else {
							userRepository.deleteByUserID(userID.toString());
							contextUserAuthRepository.deleteById(contextUserId.toString());

							log.error("Error while sending activation email, user {} has been deleted", user.getUsername());

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
	private ResponseEntity<User> addUserStandardRegistration(UserRegistration userRegistration, String locale) throws IOException {
		if (!Strings.isNullOrEmpty(userRegistration.getEmail()) && !Strings.isNullOrEmpty(userRegistration.getPassword())) {

			User user = new User(userRegistration.getEmail(), userRegistration.getUserRole());
			user.setUsername(userRegistration.getEmail());
			user.setActivationToken(portalUtils.generateToken());
			user.setEnabled(true);
			user.setActivated(false);
			user.setPasswordUpdated(true);
			user.setPassword(userRegistration.getPassword());

			// validate the user password
			String error = validateUserPasswordStandardRegistration(user);
			if (!Strings.isNullOrEmpty(error)) {
				return new ResponseEntity(new CustomErrorType(error), HttpStatus.NOT_FOUND);
			}

			user.setPassword(passwordEncoder.encode(user.getPassword()));

			// Return userId and get user with id from this id
			ObjectId userID = userRepository.saveAndReturnId(user);

			ObjectId contextId = addNewContextForStandardRegistration(user, userID);

			if (contextId != null) {
				if (mailUtils.sendEmail(user, mailUtils.generateEmailContent(user, locale), StaticConfigItems.email_subject_al)) {
					// add standard group for current user
					dataInitialization.addDefaultGroup(userID.toString(), contextId.toString());
					// Map current context with the current user
					addContextUserAuthentication(userID.toString(), contextId.toString(), getUserRoleStandardRegistration(user));
					return new ResponseEntity<User>(user, HttpStatus.OK);
				} else {

					userRepository.deleteByUserID(userID.toString());
					contextRepository.deleteByContextId(contextId.toString());

					log.error("Error while sending activation email, user {} has been deleted", user.getUsername());

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
	 * This functions creates the new context for each user which is created using standard registration.
	 *
	 * @param user
	 * @return
	 */
	private ObjectId addNewContextForStandardRegistration(User user, ObjectId userId) {
		String licensePlanName = "Default";
		LicensePlan licensePlan = licensePlanRepository.findByName(licensePlanName);

		if (licensePlan != null) {
			Context context = new Context();
			// TODO: Find better way to define context name
			String tempContextName = user.getEmail().substring(user.getEmail().indexOf("@") + 1);
			String contextName = tempContextName.substring(0, tempContextName.indexOf("."));
			context.setName(contextName + 1);
			context.setLicensePlanId(licensePlan.getId());

			log.debug("Added new context with name: {}", context.getName());

			return contextRepository.saveAndReturnId(context);

		} else {
			log.error("License Plan {} not found", licensePlanName);
		}
		return null;
	}

	/**
	 * This function validates the user password and returns null in case that password is ok. In all other cases an error will be returned
	 * and the procedure will be stopped.
	 *
	 * @param user
	 * @param locale
	 * @return
	 */
	public String validateUserPasswordStandardRegistration(User user) {
		Errors errors = new BeanPropertyBindingResult(user, "user");

		passwordValidator.validate(user, errors);

		if (errors.hasErrors()) {
			return errors.getAllErrors().get(0).getDefaultMessage();
		}
		return null;
	}

	/**
	 * This function is used to define the user role for the standard registration
	 *
	 * @param user
	 * @return
	 */
	private UserRole getUserRoleStandardRegistration(User user) {
		// TODO: This should be done somewhere on the startup, we have to add already static users with the role SUPERADMIN.
		if (user.getEmail().contains("@secinto.com")) {
			return UserRole.SUPERADMIN;
		}
		return UserRole.ADMIN;
	}

	/**
	 * This function updates the superuserIds list in each group when new superuser is added.
	 *
	 * @param userRegistration
	 * @param userId
	 * @throws ItemNotFoundRepositoryException
	 */
	private void addSuperuserTotheSelectedGroup(UserRegistration userRegistration, String userId) throws ItemNotFoundRepositoryException {
		if (userRegistration.getGroupIds() != null) {
			for (String groupId : userRegistration.getGroupIds()) {
				CompanyGroup group = groupRepository.find(groupId);
				if (group != null) {
					group.addSuperUserId(userId);
					groupRepository.update(group);
					log.debug("Superuser {} added to the following group {}", userRegistration.getEmail(), group.getName());
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<User> updateUser(UserRegistration userRegistration, String locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(userRegistration.getEmail())) {
			User user = userRepository.findByEmail(userRegistration.getEmail());
			// TODO: this function must be adapted after changes on the contextId
			if (user != null) {

				// Check if user role has changed
				if (!userRegistration.getUserRole().equals(userRegistration.getUserRole())) {
					// if old user role was admin delete it from the admin group
					if (userRegistration.getUserRole().equals(UserRole.ADMIN)) {
						// Context context = contextRepository.find(user.getContextIds());
						// if (context != null) {
						// context.removeAdmin(user.getId());
						// contextRepository.update(context);
						// } else {
						// return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
						// HttpStatus.NOT_FOUND);
						// }
					}
					// Delete the super user from all groups to which he has been assigned
					else if (userRegistration.getUserRole().equals(UserRole.SUPERUSER)) {
						// List<CompanyGroup> groups = groupRepository.findByContextId(user.getContextIds());
						// if (groups != null) {
						// for (CompanyGroup group : groups) {
						// if (!group.getSuperUserIds().contains(user.getId())) {
						// group.removeSuperUserId(user.getId());
						// groupRepository.update(group);
						// }
						// }
						// }
					}

					userRegistration.setUserRole(userRegistration.getUserRole());

					if (userRegistration.getUserRole().equals(UserRole.ADMIN)) {
						// Assign user to the context adminIds
						// Context context = contextRepository.find(user.getContextIds());
						// if (context != null) {
						// if (!context.getAdmins().contains(user.getId())) {
						// context.addAdmin(user.getId());
						// contextRepository.update(context);
						// }
						//
						// } else {
						// return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
						// HttpStatus.NOT_FOUND);
						// }
					}

					else if (userRegistration.getUserRole().equals(UserRole.SUPERUSER)) {
						if (userRegistration.getGroupIds() != null) {
							for (String groupId : userRegistration.getGroupIds()) {
								CompanyGroup group = groupRepository.find(groupId);
								if (group != null) {
									group.addSuperUserId(user.getId());
									groupRepository.update(group);
								}
							}
						}
					}
				} else {
					if (userRegistration.getGroupIds() != null) {
						// List<CompanyGroup> oldGroups = groupRepository.findByContextId(user.getContextIds());
						// if (oldGroups != null) {
						// for (CompanyGroup group : oldGroups) {
						// if (group.getSuperUserIds().contains(user.getId())) {
						// group.removeSuperUserId(user.getId());
						// groupRepository.update(group);
						// }
						// }
						// }

						for (String groupId : userRegistration.getGroupIds()) {
							CompanyGroup group = groupRepository.find(groupId);
							if (group != null) {
								group.addSuperUserId(user.getId());
								groupRepository.update(group);
							}
						}
					}
				}
				userRepository.update(user);
				return new ResponseEntity<User>(user, HttpStatus.OK);
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale)),
						HttpStatus.NOT_FOUND);
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
	 * This function adds new ContextUserAuthentication for each added user
	 *
	 * @param userId
	 * @param contextId
	 * @param userRole
	 */
	private ObjectId addContextUserAuthentication(String userId, String contextId, UserRole userRole) {

		if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(userId) && userRole != null) {
			ContextUserAuthentication contextUserAuthentication = new ContextUserAuthentication(userId, contextId, userRole);

			return contextUserAuthRepository.saveAndReturnId(contextUserAuthentication);
		}
		return null;

	}

	/**
	 * This function updates the myUsers List for the user which added the user which is currently being deleted and new privileged user will
	 * be added to this list
	 *
	 * @param addedByUser
	 * @param oldUserId
	 * @param newUserId
	 */
	public void updateAddedByUser(User addedByUser, String oldUserId, String newUserId) {
		if (addedByUser != null) {
			if (addedByUser.getMyUsers() != null) {
				addedByUser.getMyUsers().remove(oldUserId);
				if (!Strings.isNullOrEmpty(newUserId)) {
					addedByUser.addMyUser(newUserId);
				}
				try {
					userRepository.update(addedByUser);
				} catch (ItemNotFoundRepositoryException e) {
					log.error("User not found");
				}
			}
		}
	}

	/**
	 * This function deletes all user dependencies. Flag deleteMyUsers is used to distinguish if the users from the myUsers list should be
	 * deleted or not
	 *
	 * @param user
	 * @param deleteMyUsers
	 */
	public void deleteUserDependencies(User user, boolean deleteMyUsers, boolean updateMyUsers) {

		if (deleteMyUsers) {
			if (user.getMyUsers() != null) {
				for (String myUserId : user.getMyUsers()) {
					User myUser = userRepository.find(myUserId);

					if (myUser != null) {
						deleteUserDependencies(user, false, false);
					}
				}
			}
		}

		if (updateMyUsers) {
			User addedByUser = userRepository.findAddedByUser(user.getId());
			if (addedByUser != null) {
				updateAddedByUser(addedByUser, user.getId(), null);
			}
		}

		List<CompanyGroup> groups = groupRepository.findByOwnerId(user.getId());

		if (groups != null) {
			for (CompanyGroup group : groups) {
				licenseRepository.deleteByGroupId(group.getId());
				groupRepository.delete(group);
			}
		}
		ruleRepository.deleteByUserId(user.getId());
		toolRepository.deleteByUserID(user.getId());
		emailConfigRepository.deleteByUserId(user.getId());
		emailRepository.deleteByUserId(user.getId());
		notificationRepository.deleteByUserId(user.getId());
		tokenRepository.deleteByUserId(user.getId());
	}

	/**
	 * This function copies the added users from the user which is being deleted to the privileged one
	 *
	 * @param privilegedUser
	 * @param myUsers
	 * @return
	 */
	public User copyMyUsersToPrivilegedUser(User privilegedUser, List<String> myUsers) {
		for (String myUser : myUsers) {
			if (!myUser.equals(privilegedUser.getId())) {
				privilegedUser.addMyUser(myUser);
			}
		}
		return privilegedUser;
	}

	/**
	 * This function returns the privileged user from the list of myUsers
	 *
	 * @param myUsers
	 * @return
	 */
	public User getPrivilegedUser(List<String> myUsers) {
		// TODO: check this, context must also be
		User adminUser = null;
		User superUser = null;
		if (myUsers != null) {
			for (String myUserId : myUsers) {
				User myUser = userRepository.find(myUserId);
				if (myUser != null) {
					/*
					 * if (myUser.getUserRole().equals(UserRole.ADMIN)) { adminUser = myUser; } else if
					 * (myUser.getUserRole().equals(UserRole.SUPERUSER)) { superUser = myUser; }
					 */
				}
			}

			if (adminUser != null) {
				return adminUser;
			} else if (superUser != null) {
				return superUser;
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * This function checks if the user which is being deleted contains a privileged user (ADMIN or SUPERUSER)
	 *
	 * @param myUsers
	 * @return
	 */
	public boolean containsPrivilegedUser(List<String> myUsers) {
		// if (myUsers != null) {
		// for (String myUserId : myUsers) {
		// User myUser = userRepository.find(myUserId);
		// if (myUser != null) {
		// if (myUser.getUserRole().equals(UserRole.ADMIN) || myUser.getUserRole().equals(UserRole.SUPERUSER)) {
		// return true;
		// }
		// }
		//
		// }
		// }
		return false;
	}

	/**
	 * This function is used to invite the user to the current context which is included in the UserRegistration object
	 *
	 * @param userRegistration
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ResponseEntity<User> inviteUserToContext(UserRegistration userRegistration, String locale) {

		// invite is only possible if user is added by another user
		if (userRegistration.getRegistrationType().equals(UserRegistrationType.ADDED_BY_USER)) {
			if (!Strings.isNullOrEmpty(userRegistration.getAddedByUserId())) {
				// User addedByUser = userRepository.find(userRegistration.getAddedByUserId());
				// User currentUser = userRepository.findByEmail(userRegistration.getEmail());

				// If this user is already part of this context, no need to invite
				/*
				 * if (!currentUser.getContextIds().contains(userRegistration.getCurrentContextId())) {
				 *
				 * }
				 */

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
	public List<User> getAllUsersFromCurrentContext(Context context, String userId) {
		List<User> users = new ArrayList<>();
		List<ContextUserAuthentication> contextUserAuthList = contextUserAuthRepository.getByContextId(context.getId());

		if (contextUserAuthList != null) {
			for (ContextUserAuthentication contextUserAuth : contextUserAuthList) {
				if (contextUserAuth != null) {
					if (!contextUserAuth.getUserId().equals(userId)) {
						User user = userRepository.find(contextUserAuth.getUserId());
						if (user != null) {
							users.add(user);
						}
					}
				}
			}
		}

		return users;

	}

}
