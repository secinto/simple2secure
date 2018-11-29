package com.simple2secure.portal.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.UserDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.Probe;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRegistration;
import com.simple2secure.api.model.UserRegistrationType;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.DeviceRepository;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.EmailRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.ReportRepository;
import com.simple2secure.portal.repository.RuleRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.repository.TokenRepository;
import com.simple2secure.portal.repository.ToolRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.security.PasswordValidator;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.DataInitialization;
import com.simple2secure.portal.utils.MailUtils;
import com.simple2secure.portal.utils.PortalUtils;

@RestController
public class UserController {

	static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	QueryRepository queryRepository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	ToolRepository toolRepository;

	@Autowired
	ProcessorRepository processorRepository;

	@Autowired
	EmailConfigurationRepository emailConfigRepository;

	@Autowired
	EmailRepository emailRepository;

	@Autowired
	DeviceRepository deviceRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	NetworkReportRepository networkReportRepository;

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	RuleRepository ruleRepository;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	LicensePlanRepository licensePlanRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	PasswordValidator passwordValidator;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	DataInitialization dataInitialization;

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	MailUtils mailUtils;

	@Autowired
	RestTemplate restTemplate;

	protected static SecureRandom random = new SecureRandom();

	/**
	 * This function is used to add new user. For each user added it sets the configuration, queries, processors and steps from the default
	 * configuration .json files which are placed on the server
	 *
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	@RequestMapping(value = "/api/users", method = RequestMethod.POST)
	public ResponseEntity<User> insertUser(@RequestBody UserRegistration user, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, IOException, URISyntaxException {

		if (user != null) {
			if (user.getRegistrationType().equals(UserRegistrationType.ADDED_BY_USER)) {
				return addNewUser(user, locale);
			} else {
				return updateUser(user, locale);
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This function is used for the standard registration over the login page
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/register", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<User> registerUser(@RequestBody UserRegistration user, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, IOException, URISyntaxException {
		if (user != null) {
			return addNewUser(user, locale);
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This user updates user info
	 *
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@RequestMapping(value = "/api/users/update", method = RequestMethod.POST)
	public ResponseEntity<User> updateUserInfo(@RequestBody User user, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, IOException, URISyntaxException {

		if (user != null) {
			userRepository.update(user);

			return new ResponseEntity<User>(user, HttpStatus.OK);
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ResponseEntity<User> updateUser(UserRegistration userRegistration, String locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(userRegistration.getEmail())) {
			User user = userRepository.findByEmail(userRegistration.getEmail());
			// TODO: this function must be adapted after changes on the contextId
			if (user != null) {

				// Check if user role has changed
				if (!user.getUserRole().equals(userRegistration.getUserRole())) {
					// if old user role was admin delete it from the admin group
					if (user.getUserRole().equals(UserRole.ADMIN)) {
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
					else if (user.getUserRole().equals(UserRole.SUPERUSER)) {
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

					user.setUserRole(userRegistration.getUserRole());

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ResponseEntity<User> addNewUser(UserRegistration userRegistration, String locale)
			throws ItemNotFoundRepositoryException, IOException {

		ObjectId userID = null;
		if (userRegistration.getRegistrationType().equals(UserRegistrationType.ADDED_BY_USER)) {
			// Handle all action for ADDED BY USER TYPE
			if (!Strings.isNullOrEmpty(userRegistration.getEmail()) && userRegistration.getUserRole() != null) {
				User user = new User(userRegistration.getEmail(), userRegistration.getUserRole());
				user.setUsername(userRegistration.getEmail());

				if (!checkIfUserExists(user.getEmail())) {
					user.setEnabled(true);
					user.setActivationToken(portalUtils.generateToken());
					user.setActivated(false);

					String emailContent = messageByLocaleService.getMessage("registration_email_content", locale) + loadedConfigItems.getBaseURL()
							+ "/api/users/activate/" + user.getActivationToken();

					if (user.getUserRole().equals(UserRole.ADMIN)) {
						Context context = contextRepository.getContextByUserId(userRegistration.getAddedByUserId());

						if (context != null) {

							// Add current admin group to the current user
							user.addContextId(userRegistration.getCurrentContextId());
							userID = userRepository.saveAndReturnId(user);

							context.addAdmin(userID.toString());

							User addedByUser = userRepository.find(userRegistration.getAddedByUserId());

							if (addedByUser == null) {
								userRepository.deleteByUserID(userID.toString());
								return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_no_added_by_userId", locale)),
										HttpStatus.NOT_FOUND);
							} else {
								addedByUser.addMyUser(userID.toString());

								if (mailUtils.sendEmail(user, emailContent, StaticConfigItems.email_subject_al)) {
									userRepository.update(addedByUser);
									contextRepository.update(context);

									return new ResponseEntity<User>(user, HttpStatus.OK);
								} else {
									userRepository.deleteByUserID(userID.toString());
									return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_sending_email", locale)),
											HttpStatus.NOT_FOUND);
								}
							}

						}
					} else if (user.getUserRole().equals(UserRole.SUPERUSER)) {
						User addedByUser = userRepository.find(userRegistration.getAddedByUserId());

						if (addedByUser == null) {
							return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_no_added_by_userId", locale)),
									HttpStatus.NOT_FOUND);
						} else {
							user.setContextIds(addedByUser.getContextIds());
							userID = userRepository.saveAndReturnId(user);
							addedByUser.addMyUser(userID.toString());
							if (userRegistration.getGroupIds() != null && !userRegistration.getGroupIds().isEmpty()) {

								if (mailUtils.sendEmail(user, emailContent, StaticConfigItems.email_subject_al)) {
									userRepository.update(addedByUser);

									for (String groupId : userRegistration.getGroupIds()) {
										CompanyGroup group = groupRepository.find(groupId);
										if (group != null) {
											group.addSuperUserId(userID.toString());
											groupRepository.update(group);
										}
									}

									return new ResponseEntity<User>(user, HttpStatus.OK);
								} else {
									userRepository.deleteByUserID(userID.toString());
									return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_sending_email", locale)),
											HttpStatus.NOT_FOUND);
								}
							} else {
								userRepository.deleteByUserID(userID.toString());
							}

						}
					} else if (user.getUserRole().equals(UserRole.USER)) {

						User addedByUser = userRepository.find(userRegistration.getAddedByUserId());

						if (addedByUser == null) {
							return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_no_added_by_userId", locale)),
									HttpStatus.NOT_FOUND);
						} else {
							user.setContextIds(addedByUser.getContextIds());
							userID = userRepository.saveAndReturnId(user);
							addedByUser.addMyUser(userID.toString());

							if (mailUtils.sendEmail(user, emailContent, StaticConfigItems.email_subject_al)) {
								userRepository.update(addedByUser);
								return new ResponseEntity<User>(user, HttpStatus.OK);
							} else {
								userRepository.deleteByUserID(userID.toString());
								return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_sending_email", locale)),
										HttpStatus.NOT_FOUND);
							}
						}
					}
				} else {
					// If user already exists call inviterUserToContext
					inviterUserToContext(userRegistration);
					return new ResponseEntity(
							new CustomErrorType(messageByLocaleService.getMessage("user_with_provided_email_already_exists", locale)),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
						HttpStatus.NOT_FOUND);
			}
		} else if (userRegistration.getRegistrationType().equals(UserRegistrationType.STANDARD)) {
			if (!Strings.isNullOrEmpty(userRegistration.getEmail()) && !Strings.isNullOrEmpty(userRegistration.getPassword())) {

				User user = new User(userRegistration.getEmail(), userRegistration.getUserRole());
				user.setUsername(userRegistration.getEmail());

				if (!checkIfUserExists(user.getEmail())) {
					user.setActivationToken(portalUtils.generateToken());
					user.setEnabled(true);
					user.setActivated(false);
					user.setPasswordUpdated(true);

					String emailContent = messageByLocaleService.getMessage("registration_email_content", locale) + loadedConfigItems.getBaseURL()
							+ "/api/users/activate/" + user.getActivationToken();

					if (user.getEmail().contains("@secinto.com")) {
						user.setUserRole(UserRole.SUPERADMIN);
					} else {
						user.setUserRole(UserRole.ADMIN);
					}

					user.setPassword(userRegistration.getPassword());

					Errors errors = new BeanPropertyBindingResult(user, "user");

					passwordValidator.validate(user, errors);

					if (errors.hasErrors()) {

						String error = errors.getAllErrors().get(0).getDefaultMessage();

						return new ResponseEntity(new CustomErrorType(error), HttpStatus.NOT_FOUND);
					}

					user.setPassword(passwordEncoder.encode(user.getPassword()));

					userID = userRepository.saveAndReturnId(user);

					LicensePlan licensePlan = licensePlanRepository.findByName("Default");

					if (licensePlan != null) {
						Context context = new Context();
						String tempContextName = user.getEmail().substring(user.getEmail().indexOf("@") + 1);
						String contextName = tempContextName.substring(0, tempContextName.indexOf("."));
						context.setName(contextName + 1);
						context.setLicensePlanId(licensePlan.getId());
						context.addAdmin(userID.toString());
						ObjectId contextId = contextRepository.saveAndReturnId(context);

						// This is only for test!
						context.setName(contextName + 2);
						ObjectId contextId2 = contextRepository.saveAndReturnId(context);

						if (mailUtils.sendEmail(user, emailContent, StaticConfigItems.email_subject_al)) {

							dataInitialization.addDefaultGroup(userID.toString(), contextId.toString());

							// TEST ONLY
							dataInitialization.addDefaultGroup(userID.toString(), contextId2.toString());

							User userWithId = userRepository.find(userID.toString());

							// Adding new admin group for the current user
							userWithId.addContextId(contextId.toString());

							UserRole userRole = UserRole.ADMIN;

							if (user.getEmail().contains("@secinto.com")) {
								userRole = UserRole.SUPERADMIN;
							}

							// Mapping the userId with the contextId and role
							ContextUserAuthentication contextUserAuthentication = new ContextUserAuthentication(userWithId.getId(), contextId.toString(),
									userRole);

							contextUserAuthRepository.save(contextUserAuthentication);

							contextUserAuthentication = new ContextUserAuthentication(userWithId.toString(), contextId2.toString(), userRole);
							contextUserAuthRepository.save(contextUserAuthentication);

							// TEST ONLY
							userWithId.addContextId(contextId2.toString());
							userRepository.update(userWithId);

							return new ResponseEntity<User>(user, HttpStatus.OK);
						} else {
							userRepository.deleteByUserID(userID.toString());
							contextRepository.deleteByContextId(contextId.toString());
							return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
									HttpStatus.NOT_FOUND);
						}

					} else {
						// License plan not found!
						userRepository.deleteByUserID(userID.toString());
						return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
								HttpStatus.NOT_FOUND);
					}

				} else {
					return new ResponseEntity(
							new CustomErrorType(messageByLocaleService.getMessage("user_with_provided_email_already_exists", locale)),
							HttpStatus.NOT_FOUND);
				}

			}

		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 *
	 * @param userRegistration
	 */
	private void inviterUserToContext(UserRegistration userRegistration) {

		// invite is only possible if user is added by another user
		if (userRegistration.getRegistrationType().equals(UserRegistrationType.ADDED_BY_USER)) {
			if (!Strings.isNullOrEmpty(userRegistration.getAddedByUserId())) {
				User addedByUser = userRepository.find(userRegistration.getAddedByUserId());
				User currentUser = userRepository.findByEmail(userRegistration.getEmail());

				// If this user is already part of this context, no need to invite
				if (!currentUser.getContextIds().contains(userRegistration.getCurrentContextId())) {

				}

			}

		}

		String userName = userRegistration.getEmail();
		String addedById = userRegistration.getAddedByUserId();
		String test = userName;
		String test2 = addedById;

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
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/activate/updatePassword/{authenticationToken}", method = RequestMethod.POST)
	public ResponseEntity<User> updateUserPasswordFirstLogin(@PathVariable("authenticationToken") String authenticationToken,
			@RequestBody String password, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, URISyntaxException, IOException {

		if (!Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(authenticationToken)) {
			User user = userRepository.findByActivationToken(authenticationToken);

			if (user != null) {
				user.setActivated(true);
				user.setPassword(password);

				Errors errors = new BeanPropertyBindingResult(user, "user");

				passwordValidator.validate(user, errors);

				if (errors.hasErrors()) {

					String error = errors.getAllErrors().get(0).getDefaultMessage();

					return new ResponseEntity(new CustomErrorType(error), HttpStatus.NOT_FOUND);
				}

				user.setPassword(passwordEncoder.encode(user.getPassword()));
				user.setPasswordUpdated(true);

				userRepository.update(user);

				URI url = new URI(loadedConfigItems.getBaseURLWeb() + "/#/account/activate/" + user.getActivationToken());
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setLocation(url);
				return new ResponseEntity<>(user, httpHeaders, HttpStatus.OK);

			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale)),
						HttpStatus.NOT_FOUND);
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
					HttpStatus.NOT_FOUND);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/activate/{activationToken}", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> activateUser(@PathVariable("activationToken") String activationToken,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException, URISyntaxException, IOException {
		User user = userRepository.findByActivationToken(activationToken);

		if (user != null) {

			if (!user.isActivated()) {
				if (user.isPasswordUpdated()) {
					user.setActivated(true);
					userRepository.update(user);
				}

				URI url = new URI(loadedConfigItems.getBaseURLWeb() + "/#/account/activate/" + activationToken);

				if (!user.isPasswordUpdated()) {
					url = new URI(loadedConfigItems.getBaseURLWeb() + "/#/account/updatePassword/" + activationToken);
				}

				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setLocation(url);
				return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
			} else {
				URI url = new URI(loadedConfigItems.getBaseURLWeb() + "/#/account/activated/");
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setLocation(url);
				return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("activation_token_not_valid", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/sendResetPasswordEmail", method = RequestMethod.POST)
	public ResponseEntity<User> sendResetPasswordEmail(@RequestBody String email, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, IOException {

		if (!Strings.isNullOrEmpty(email)) {
			User user = userRepository.findByEmailOnlyActivated(email);
			if (user != null) {
				user.setPasswordResetToken(portalUtils.generateToken());
				user.setPasswordResetExpirationTime(System.currentTimeMillis() + StaticConfigItems.expiration_time_password_reset);

				userRepository.update(user);

				// Add template for the email content and retrieve it from the database!
				String emailContent = "In order to change your password please click on the link below. \n\n Password reset link: "
						+ loadedConfigItems.getBaseURL() + "/api/users/resetPassword/" + user.getPasswordResetToken();

				if (mailUtils.sendEmail(user, emailContent, StaticConfigItems.email_subject_pr)) {
					return new ResponseEntity<User>(user, HttpStatus.OK);
				} else {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_sending_email", locale)),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("user_with_provided_email_not_exists", locale)),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws URISyntaxException
	 */
	@RequestMapping(value = "/api/users/resetPassword/{token}", method = RequestMethod.GET)
	public ResponseEntity<User> showChangePasswordPage(@PathVariable("token") String token, @RequestHeader("Accept-Language") String locale)
			throws URISyntaxException {
		URI url = new URI(loadedConfigItems.getBaseURLWeb() + "/#/resetPassword/" + token);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(url);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/updatePassword/{token}", method = RequestMethod.POST)
	public ResponseEntity<User> updateUserPassword(@PathVariable("token") String token, @RequestBody String password,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException, URISyntaxException, IOException {

		if (!Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(token)) {
			User user = userRepository.findByPasswordResetToken(token);

			if (user != null) {
				if (user.getPasswordResetExpirationTime() >= System.currentTimeMillis()) {

					// TODO - check if token is still valid!!!

					user.setPassword(password);

					Errors errors = new BeanPropertyBindingResult(user, "user");

					passwordValidator.validate(user, errors);

					if (errors.hasErrors()) {

						String error = errors.getAllErrors().get(0).getDefaultMessage();

						return new ResponseEntity(new CustomErrorType(error), HttpStatus.NOT_FOUND);
					}

					user.setPassword(passwordEncoder.encode(password));
					user.setPasswordResetToken(token);
					userRepository.update(user);

					String emailContent = messageByLocaleService.getMessage("password_changed_email_content", locale);
					mailUtils.sendEmail(user, emailContent, StaticConfigItems.email_subjct_pcs);

					URI url = new URI(loadedConfigItems.getBaseURLWeb());
					HttpHeaders httpHeaders = new HttpHeaders();
					httpHeaders.setLocation(url);
					return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
				} else {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("password_reset_token_expired", locale)),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_token_already_used", locale)),
						HttpStatus.NOT_FOUND);
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
					HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * This function returns all users from the user repository
	 */
	@RequestMapping(value = "/api/users", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<User>> getUsers(@RequestHeader("Accept-Language") String locale) {
		List<User> userList = userRepository.findAll();
		return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
	}

	/**
	 * This function returns all context for the provided user
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/api/users/context/{userId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Context>> getContextsByUserId(@PathVariable("userId") String userId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(userId)) {
			List<Context> contextList = userRepository.findAssignedContextsByUserId(userId);
			return new ResponseEntity<List<Context>>(contextList, HttpStatus.OK);
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function finds and returns user according to the user id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/api/users/{id}/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<UserDTO> getUserByID(@PathVariable("id") String id, @PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		User user = userRepository.find(id);
		List<User> myUsers = new ArrayList<>();

		if (user != null && !Strings.isNullOrEmpty(contextId)) {

			// Retrieving the admingGroup according to the current selected group in the web
			Context context = contextRepository.find(contextId);

			// Retrieving my groups
			List<CompanyGroup> myGroups = new ArrayList<>();
			List<CompanyGroup> myGroupsWithChildren = new ArrayList<>();
			if (context != null) {
				myGroups = groupRepository.findRootGroupsByContextId(context.getId());
				if (myGroups == null) {
					myGroups = new ArrayList<>();
				} else {
					for (CompanyGroup group : myGroups) {
						if (!PortalUtils.groupHasChildren(group)) {
							myGroupsWithChildren.add(group);
						} else {
							myGroupsWithChildren.add(groupTraverse(group));
						}
					}
				}
			}

			/* Set user users from the userRepository by the addedByUserId and context */
			List<String> myUserIds = user.getMyUsers();
			if (myUserIds == null) {
				myUserIds = new ArrayList<>();
			} else {
				for (String userId : myUserIds) {
					User myUser = userRepository.find(userId);
					if (myUser != null) {
						if (!myUser.getUserRole().equals(UserRole.SUPERADMIN)) {
							myUsers.add(myUser);
						}
					}

				}
			}

			if (context != null) {
				for (String userId : context.getAdmins()) {
					if (!userId.equals(user.getId())) {

						User adminUser = userRepository.find(userId);

						if (adminUser != null) {
							if (!myUsers.contains(adminUser)) {
								if (!adminUser.getUserRole().equals(UserRole.SUPERADMIN)) {
									myUsers.add(adminUser);
								}
							}

							if (!adminUser.getMyUsers().isEmpty()) {
								for (String adminChildId : adminUser.getMyUsers()) {
									User adminChildUser = userRepository.find(adminChildId);
									if (adminChildUser != null) {
										if (!adminChildUser.getId().equals(user.getId())) {
											myUsers.add(adminChildUser);
										}
									}
								}
							}
						}
					}
				}
			}

			/* Set user probes from the licenses - not from the users anymore */
			List<Probe> myProbes = new ArrayList<Probe>();
			List<CompanyGroup> assignedGroups = groupRepository.findByContextId(contextId);
			if (myGroups != null) {
				for (CompanyGroup group : assignedGroups) {
					List<CompanyLicensePrivate> licenses = licenseRepository.findByGroupId(group.getId());
					if (licenses != null) {
						for (CompanyLicensePrivate license : licenses) {
							if (license.isActivated()) {
								Probe probe = new Probe(license.getProbeId(), group, license.isActivated());
								myProbes.add(probe);
							}
						}
					}
				}
			}

			user.setMyProbes(myProbes);

			UserDTO userDTO = new UserDTO(user, myUsers, myGroupsWithChildren);
			return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
		}

		else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This function deletes configuration and user according to the user id
	 *
	 * @param userId
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<User> deleteUser(@PathVariable("id") String userId, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {
		boolean hasPrivilegedUser = false;
		if (!Strings.isNullOrEmpty(userId)) {

			User user = userRepository.find(userId);

			if (user != null) {
				// If user added users check if contains privileged(ADMIN or SUPERUSER)
				if (user.getMyUsers() != null) {
					hasPrivilegedUser = containsPrivilegedUser(user.getMyUsers());

					// If user contains privileged user(ADMIN or SUPERUSER) - copy groups and own users to one of them
					if (hasPrivilegedUser) {
						User privilegedUser = getPrivilegedUser(user.getMyUsers());

						if (privilegedUser != null) {
							privilegedUser = copyMyUsersToPrivilegedUser(privilegedUser, user.getMyUsers());
							copyMyGroupsToPrivilegedUser(privilegedUser, user);
							userRepository.update(privilegedUser);
							User addedByUser = userRepository.findAddedByUser(user.getId());
							if (addedByUser != null) {
								updateAddedByUser(addedByUser, user.getId(), privilegedUser.getId());
							}
							deleteUserDependencies(user, false, false);
						}
					} else {
						deleteUserDependencies(user, true, true);
					}
				} else {
					deleteUserDependencies(user, true, true);
				}

				userRepository.delete(user);

				return new ResponseEntity<User>(user, HttpStatus.OK);
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_user", locale)),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_user", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This function updates the myUsers List for the user which added the user which is currently being deleted and new privileged user will
	 * be added to this list
	 *
	 * @param addedByUser
	 * @param oldUserId
	 * @param newUserId
	 */
	private void updateAddedByUser(User addedByUser, String oldUserId, String newUserId) {
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
	 * This function copies the groups from the user which is being deleted to the privileged one
	 *
	 * @param privilegedUser
	 * @param user
	 */
	private void copyMyGroupsToPrivilegedUser(User privilegedUser, User user) {
		List<CompanyGroup> groups = groupRepository.findByOwnerId(user.getId());

		if (groups != null) {
			for (CompanyGroup group : groups) {
				// group.setAddedByUserId(privilegedUser.getId());
				// group.setOwner(privilegedUser.getEmail());
				try {
					groupRepository.update(group);
				} catch (ItemNotFoundRepositoryException e) {
					log.error("Group not found");
				}
			}
		}
	}

	/**
	 * This function copies the added users from the user which is being deleted to the privileged one
	 *
	 * @param privilegedUser
	 * @param myUsers
	 * @return
	 */
	private User copyMyUsersToPrivilegedUser(User privilegedUser, List<String> myUsers) {
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
	private User getPrivilegedUser(List<String> myUsers) {
		User adminUser = null;
		User superUser = null;
		if (myUsers != null) {
			for (String myUserId : myUsers) {
				User myUser = userRepository.find(myUserId);
				if (myUser != null) {
					if (myUser.getUserRole().equals(UserRole.ADMIN)) {
						adminUser = myUser;
					} else if (myUser.getUserRole().equals(UserRole.SUPERUSER)) {
						superUser = myUser;
					}
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
	private boolean containsPrivilegedUser(List<String> myUsers) {
		if (myUsers != null) {
			for (String myUserId : myUsers) {
				User myUser = userRepository.find(myUserId);
				if (myUser != null) {
					if (myUser.getUserRole().equals(UserRole.ADMIN) || myUser.getUserRole().equals(UserRole.SUPERUSER)) {
						return true;
					}
				}

			}
		}
		return false;
	}

	/**
	 * This function deletes all user dependencies. Flag deleteMyUsers is used to distinguish if the users from the myUsers list should be
	 * deleted or not
	 *
	 * @param user
	 * @param deleteMyUsers
	 */
	private void deleteUserDependencies(User user, boolean deleteMyUsers, boolean updateMyUsers) {

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
	 * This function returns all devices according to the user id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/deleteProbe/{probeId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<CompanyLicensePrivate> deleteProbe(@PathVariable("probeId") String probeId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(probeId)) {
			// retrieve license from database
			CompanyLicensePrivate license = licenseRepository.findByProbeId(probeId);
			if (license != null) {
				// TODO - check before deleting if we need to decrement the number of downloaded licenses in context
				licenseRepository.delete(license);
				return new ResponseEntity<>(license, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_probe", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all devices according to the user id
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/changeGroup/{probeId}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<CompanyLicensePrivate> changeGroupProbe(@PathVariable("probeId") String probeId, @RequestBody CompanyGroup group,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(probeId) && group != null) {
			// retrieve license from database
			CompanyLicensePrivate license = licenseRepository.findByProbeId(probeId);
			CompanyGroup dbGroup = groupRepository.find(group.getId());
			if (license != null && dbGroup != null) {

				license.setGroupId(dbGroup.getId());
				// TODO - check what needs to be updated in order that probe gets a correct
				// during the next license check
				licenseRepository.update(license);
				return new ResponseEntity<>(license, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_probe_group", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all devices according to the user id
	 */
	@RequestMapping(value = "/api/users/devices/{userID}")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Probe>> getProbesByUserID(@PathVariable("userID") String userId,
			@RequestHeader("Accept-Language") String locale) {

		List<Probe> probes = new ArrayList<Probe>();

		List<CompanyLicensePrivate> licenses = licenseRepository.findByUserId(userId);

		if (licenses != null) {
			for (CompanyLicensePrivate license : licenses) {
				// Retrieve only activated probes
				if (license.isActivated()) {
					CompanyGroup group = groupRepository.find(license.getGroupId());
					if (group != null) {
						Probe probe = new Probe(license.getProbeId(), group, license.isActivated());
						probes.add(probe);
					}
				}
			}
		}
		return new ResponseEntity<List<Probe>>(probes, HttpStatus.OK);
	}

	// GROUP MANAGEMENT

	/**
	 * This function add new group to the group repository
	 *
	 * @param group
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/group/{userId}/{parentGroupId}/{contextId}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> addGroup(@RequestBody CompanyGroup group, @PathVariable("userId") String userId,
			@PathVariable("parentGroupId") String parentGroupId, @PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (group != null && !Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(contextId)) {
			User user = userRepository.find(userId);
			if (Strings.isNullOrEmpty(group.getId()) && user != null) {

				if (!parentGroupId.equals("null")) {
					// THERE IS A PARENT GROUP!!
					CompanyGroup parentGroup = groupRepository.find(parentGroupId);
					if (parentGroup != null) {
						if (user.getUserRole().equals(UserRole.SUPERUSER)) {
							group.addSuperUserId(user.getId());
						}

						group.setContextId(parentGroup.getContextId());
						group.setRootGroup(false);
						group.setParentId(parentGroupId);
						ObjectId groupId = groupRepository.saveAndReturnId(group);
						parentGroup.addChildrenId(groupId.toString());
						groupRepository.update(parentGroup);
						return new ResponseEntity<CompanyGroup>(group, HttpStatus.OK);
					} else {
						return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_group", locale)),
								HttpStatus.NOT_FOUND);
					}
				} else {
					// NEW PARENT GROUP!
					//
					Context context = contextRepository.find(contextId);
					if (context != null) {

						if (user.getUserRole().equals(UserRole.SUPERUSER)) {
							group.addSuperUserId(user.getId());
						}

						group.setContextId(context.getId());
						group.setRootGroup(true);
						groupRepository.save(group);
						return new ResponseEntity<CompanyGroup>(group, HttpStatus.OK);
					} else {
						return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_group", locale)),
								HttpStatus.NOT_FOUND);
					}
				}

			} else {
				// UPDATING EXISTING GROUP

				groupRepository.update(group);
				return new ResponseEntity<CompanyGroup>(group, HttpStatus.OK);
			}
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_group", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This function returns all users from the user repository
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/group/{groupID}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> getGroup(@PathVariable("groupID") String groupId, @RequestHeader("Accept-Language") String locale) {
		CompanyGroup group = groupRepository.find(groupId);
		if (group == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale)),
					HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<CompanyGroup>(group, HttpStatus.OK);
		}

	}

	/**
	 * This function returns all users from the user repository
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/group/user/{userId}/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<CompanyGroup>> getGroupsByUserId(@PathVariable("userId") String userId,
			@PathVariable("contextId") String contextId, @RequestHeader("Accept-Language") String locale) {
		User user = userRepository.find(userId);

		if (user != null && !Strings.isNullOrEmpty(contextId)) {
			Context context = contextRepository.find(contextId);

			if (context != null) {
				List<CompanyGroup> groups = groupRepository.findByContextId(context.getId());
				if (groups != null) {
					return new ResponseEntity<List<CompanyGroup>>(groups, HttpStatus.OK);
				} else {
					return new ResponseEntity(
							new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale)),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale)),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	// /**
	// * This function returns all groups by superuserId and contextId
	// */
	// @SuppressWarnings({ "unchecked", "rawtypes" })
	// @RequestMapping(value = "/api/users/group/superuser/{userId}", method = RequestMethod.GET)
	// @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	// public ResponseEntity<List<String>> getGroupsBySuperUserId(@PathVariable("userId") String userId,
	// @RequestHeader("Accept-Language") String locale) {
	// User user = userRepository.find(userId);
	// List<String> superUserGroups = new ArrayList<>();
	// if (user != null) {
	// List<CompanyGroup> groups = groupRepository.findBySuperUserId(userId, user.getContextIds());
	//
	// if (groups != null) {
	// for (CompanyGroup group : groups) {
	// if (group != null) {
	// superUserGroups.add(group.getId());
	// }
	// }
	// return new ResponseEntity<List<String>>(superUserGroups, HttpStatus.OK);
	// } else {
	// return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale)),
	// HttpStatus.NOT_FOUND);
	// }
	// } else {
	// return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale)),
	// HttpStatus.NOT_FOUND);
	// }
	// }

	/**
	 * This function returns all users from the user repository
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/group/{groupID}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<?> deleteGroup(@PathVariable("groupID") String groupId, @RequestHeader("Accept-Language") String locale) {
		// When the group is deleted we have also to delete the processors, configuration, etc.
		CompanyGroup group = groupRepository.find(groupId);
		if (group == null) {
			return new ResponseEntity<>(
					new CustomErrorType(
							messageByLocaleService.getMessage("problem_occured_while_retrieving_group", ObjectUtils.toObjectArray(groupId), locale)),
					HttpStatus.NOT_FOUND);
		} else {
			if (!group.isStandardGroup()) {
				deleteGroup(groupId);
				return new ResponseEntity<>(group, HttpStatus.OK);
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("standard_group_delete_error", locale)),
						HttpStatus.NOT_FOUND);
			}

		}
	}

	/**
	 * This function moves the group to the one which has been selected using drag&drop
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/groups/move/{sourceGroupId}/{destGroupId}/{userId}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> groupDragAndDrop(@PathVariable("sourceGroupId") String sourceGroupId,
			@PathVariable("destGroupId") String destGroupId, @PathVariable("userId") String userId,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		CompanyGroup sourceGroup = groupRepository.find(sourceGroupId);
		CompanyGroup toGroup = groupRepository.find(destGroupId);
		User user = userRepository.find(userId);

		if (sourceGroup != null && user != null) {
			return checkIfGroupCanBeMoved(sourceGroup, toGroup, user, locale);
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_moving_group", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This function checks the user role, which wants to move the group. Superadmins can move every group. Superusers can move only assigned
	 * groups(both fromGroup and toGroup must be assigned to the super user). Admin can move every group if it belongs to the context to which
	 * this admin belongs.
	 *
	 * @param fromGroup
	 * @param toGroup
	 * @param user
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ResponseEntity<CompanyGroup> checkIfGroupCanBeMoved(CompanyGroup fromGroup, CompanyGroup toGroup, User user, String locale)
			throws ItemNotFoundRepositoryException {

		// SUPERADMIN
		if (user.getUserRole().equals(UserRole.SUPERADMIN)) {
			// SUPERADMIN can move everything
			if (moveGroup(fromGroup, toGroup)) {
				return new ResponseEntity<CompanyGroup>(fromGroup, HttpStatus.OK);
			}
		}
		// SUPERUSER
		else if (user.getUserRole().equals(UserRole.SUPERUSER)) {
			// Move only if superuser belongs to the both groups(both groups are assigned to him)
			if (fromGroup.getSuperUserIds().contains(user.getId())) {
				// in this case the moved group will be root group
				if (toGroup == null) {
					if (moveGroup(fromGroup, toGroup)) {
						return new ResponseEntity<CompanyGroup>(fromGroup, HttpStatus.OK);
					}
				} else {
					// check if toGroup is assigned to this user
					if (toGroup.getSuperUserIds().contains(user.getId())) {
						if (moveGroup(fromGroup, toGroup)) {
							return new ResponseEntity<CompanyGroup>(fromGroup, HttpStatus.OK);
						}
					}
				}
			}
		}
		// ADMIN
		else if (user.getUserRole().equals(UserRole.ADMIN)) {

			if (!Strings.isNullOrEmpty(fromGroup.getContextId())) {
				// check if contextId of the from Group is same as contextId assigned to the user
				if (user.getContextIds().contains(fromGroup.getContextId())) {
					// In case that toGroup is null, fromGroup will be new root group
					if (toGroup == null) {
						if (moveGroup(fromGroup, toGroup)) {
							return new ResponseEntity<CompanyGroup>(fromGroup, HttpStatus.OK);
						}
					} else {
						if (!Strings.isNullOrEmpty(toGroup.getContextId())) {
							// Check if fromGroup contextId is same as toGroup contextId. If both are same move the group
							if (fromGroup.getContextId().equals(toGroup.getContextId())) {
								if (moveGroup(fromGroup, toGroup)) {
									return new ResponseEntity<CompanyGroup>(fromGroup, HttpStatus.OK);
								}
							}
						}
					}
				}
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_moving_group", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function moves the group to the destination group. If source group was root group in this function it will be unset. Children to
	 * the destination group will be added in this function. If toGroup is null it means that fromGroup will be declared as new root group.
	 *
	 * @param fromGroup
	 * @param toGroup
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	private boolean moveGroup(CompanyGroup fromGroup, CompanyGroup toGroup) throws ItemNotFoundRepositoryException {
		if (toGroup != null) {
			CompanyGroup parentGroup = groupRepository.find(fromGroup.getParentId());
			if (parentGroup != null) {
				parentGroup.removeChildrenId(fromGroup.getId());
				groupRepository.update(parentGroup);

				fromGroup.setParentId(toGroup.getId());
				toGroup.addChildrenId(fromGroup.getId());

				groupRepository.update(fromGroup);
				groupRepository.update(toGroup);
				return true;
			}
			// This is the root group!
			else {
				if (fromGroup.isRootGroup()) {
					fromGroup.setParentId(toGroup.getId());
					fromGroup.setRootGroup(false);
					toGroup.addChildrenId(fromGroup.getId());

					groupRepository.update(fromGroup);
					groupRepository.update(toGroup);
					return true;
				}
				return false;
			}
		} else {
			CompanyGroup parentGroup = groupRepository.find(fromGroup.getParentId());
			if (parentGroup != null) {
				parentGroup.removeChildrenId(fromGroup.getId());
				groupRepository.update(parentGroup);

				fromGroup.setParentId(null);
				fromGroup.setRootGroup(true);

				groupRepository.update(fromGroup);
				return true;
			}
			// In other case no need to move because this is already root group
			return false;
		}
	}

	private void deleteGroup(String groupId) {
		// Delete the group configurations
		configRepository.deleteByGroupId(groupId);

		// Delete the group steps
		stepRepository.deleteByGroupId(groupId);

		// Delete the group processors
		processorRepository.deleteByGroupId(groupId);

		// Delete all licenses and all probe reports which were created
		List<CompanyLicensePrivate> licenses = licenseRepository.findByGroupId(groupId);

		if (licenses != null) {
			for (CompanyLicensePrivate license : licenses) {
				if (!Strings.isNullOrEmpty(license.getProbeId())) {
					reportRepository.deleteByProbeId(license.getProbeId());
					networkReportRepository.deleteByProbeId(license.getProbeId());
				}
				licenseRepository.delete(license);
			}
		}

		// Remove OSQuery configuration
		queryRepository.deleteByGroupId(groupId);

		deleteGroupFromChildren(groupId);
	}

	private void deleteGroupFromChildren(String groupId) {
		CompanyGroup group = groupRepository.find(groupId);

		if (group != null) {
			if (!group.isRootGroup()) {
				CompanyGroup parent = groupRepository.find(group.getParentId());

				if (parent != null) {
					parent.removeChildrenId(groupId);
					try {
						groupRepository.update(parent);
					} catch (ItemNotFoundRepositoryException e) {
						log.error("Parent group not found {}", e);
					}
				}

				if (PortalUtils.groupHasChildren(group)) {
					deleteGroupChildren(group);
				}
			} else {
				if (PortalUtils.groupHasChildren(group)) {
					deleteGroupChildren(group);
				}
			}

			if (!group.isStandardGroup()) {
				groupRepository.delete(group);
			}

		}
	}

	private void deleteGroupChildren(CompanyGroup parentGroup) {
		List<CompanyGroup> children = getGroupChildren(parentGroup);

		for (CompanyGroup group : children) {
			if (PortalUtils.groupHasChildren(group)) {
				deleteGroupChildren(group);
				deleteGroup(group.getId());
			} else {
				deleteGroup(group.getId());
			}
		}
	}

	private List<CompanyGroup> getGroupChildren(CompanyGroup group) {
		if (PortalUtils.groupHasChildren(group)) {
			return groupRepository.findByParentId(group.getId());
		} else {
			return null;
		}
	}

	private CompanyGroup groupTraverse(CompanyGroup root) {
		List<CompanyGroup> myChildren = new ArrayList<>();
		List<CompanyGroup> allChildren = groupRepository.findByParentId(root.id);
		if (allChildren != null && allChildren.size() > 0) {
			for (CompanyGroup child : allChildren) {
				if (PortalUtils.groupHasChildren(child)) {
					List<CompanyGroup> children = getGroupChildren(child);
					child.setChildren(children);
					myChildren.add(child);

					for (CompanyGroup childItem : children) {
						groupTraverse(childItem);
					}
				} else {
					myChildren.add(child);
				}
			}
		}
		root.setChildren(myChildren);
		return root;
	}
}
