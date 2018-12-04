package com.simple2secure.portal.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.ContextDTO;
import com.simple2secure.api.dto.UserDTO;
import com.simple2secure.api.dto.UserRoleDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.CurrentContext;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.Probe;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRegistration;
import com.simple2secure.api.model.UserRegistrationType;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.CurrentContextRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.security.PasswordValidator;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.ContextUtils;
import com.simple2secure.portal.utils.DataInitialization;
import com.simple2secure.portal.utils.GroupUtils;
import com.simple2secure.portal.utils.MailUtils;
import com.simple2secure.portal.utils.PortalUtils;
import com.simple2secure.portal.utils.ProbeUtils;
import com.simple2secure.portal.utils.UserUtils;

@RestController
@RequestMapping("/api/user")
public class UserController {

	static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	CurrentContextRepository currentContextRepository;

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
	PortalUtils portalUtils;

	@Autowired
	MailUtils mailUtils;

	@Autowired
	UserUtils userUtils;

	@Autowired
	GroupUtils groupUtils;

	@Autowired
	ProbeUtils probeUtils;

	@Autowired
	ContextUtils contextUtils;

	@Autowired
	DataInitialization dataInitialization;

	/**
	 * This function returns all users from the user repository
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<User>> getUsers(@RequestHeader("Accept-Language") String locale) {
		List<User> userList = userRepository.findAll();
		return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
	}

	/**
	 * This function finds and returns user according to the user id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/{userId}/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<UserDTO> getUserByID(@PathVariable("userId") String userId, @PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		User user = userRepository.find(userId);

		if (user != null && !Strings.isNullOrEmpty(contextId)) {

			// Retrieving the context according to the current active context
			Context context = contextRepository.find(contextId);

			if (context != null) {
				List<CompanyGroup> groups = groupUtils.getAllGroupsByContextId(context);
				List<UserRoleDTO> myUsers = userUtils.getAllUsersFromCurrentContext(context, user.getId());
				List<Probe> myProbes = probeUtils.getAllProbesFromCurrentContext(context);
				List<Context> myContexts = contextUtils.getContextsByUserId(user);
				UserDTO userDTO = new UserDTO(user, myUsers, groups, myProbes, myContexts);
				return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale)),
				HttpStatus.NOT_FOUND);

	}

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
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<User> insertUser(@RequestBody UserRegistration user, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, IOException, URISyntaxException {

		if (user != null) {
			if (user.getRegistrationType().equals(UserRegistrationType.ADDED_BY_USER)) {
				return userUtils.addNewUser(user, locale);
			} else {
				return userUtils.updateUser(user, locale);
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
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<User> registerUser(@RequestBody UserRegistration user, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, IOException, URISyntaxException {
		if (user != null) {
			return userUtils.addNewUser(user, locale);
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
	@RequestMapping(value = "/update", method = RequestMethod.POST)
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

	/**
	 * This function is used to update the password after clicking on the activation link. This is used only for the users which are added by
	 * another user.
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/activate/updatePassword/{authenticationToken}", method = RequestMethod.POST)
	public ResponseEntity<User> updateUserPasswordFirstLogin(@PathVariable("authenticationToken") String authenticationToken,
			@RequestBody String password, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, URISyntaxException, IOException {

		if (!Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(authenticationToken)) {
			User user = userRepository.findByActivationToken(authenticationToken);

			if (user != null) {
				user.setActivated(true);
				user.setPassword(password);

				String error = userUtils.validateUserPassword(user);

				if (!Strings.isNullOrEmpty(error)) {
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

	/**
	 * This function activates the user, after the user has clicked on the link in the activation email which has been sent after successful
	 * registration
	 *
	 * @param activationToken
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/activate/{activationToken}", method = RequestMethod.GET)
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
	 * This function sends password reset email with the generated link to the user.
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/sendResetPasswordEmail", method = RequestMethod.POST)
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
	 * This function only redirects the user to the correct page in the web, after the user clicks on the link in the password reset email.
	 *
	 * @param user
	 * @return
	 * @throws URISyntaxException
	 */
	@RequestMapping(value = "/resetPassword/{token}", method = RequestMethod.GET)
	public ResponseEntity<User> showChangePasswordPage(@PathVariable("token") String token, @RequestHeader("Accept-Language") String locale)
			throws URISyntaxException {
		URI url = new URI(loadedConfigItems.getBaseURLWeb() + "/#/resetPassword/" + token);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(url);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}

	/**
	 * This function updates the user password with the token sent in the password reset email.
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/updatePassword/{token}", method = RequestMethod.POST)
	public ResponseEntity<User> updateUserPassword(@PathVariable("token") String token, @RequestBody String password,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException, URISyntaxException, IOException {

		if (!Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(token)) {
			User user = userRepository.findByPasswordResetToken(token);

			if (user != null) {
				if (user.getPasswordResetExpirationTime() >= System.currentTimeMillis()) {

					// TODO - check if token is still valid!!!

					user.setPassword(password);

					String error = userUtils.validateUserPassword(user);

					if (!Strings.isNullOrEmpty(error)) {
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
	 * This function deletes the user from the current context
	 *
	 * @param userId
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{userId}/{contextId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<ContextUserAuthentication> deleteUser(@PathVariable("userId") String userId,
			@PathVariable("contextId") String contextId, @RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		// TODO: Define it so that user with ADMIN or SUPERADMIN must have at least one context
		if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(contextId)) {

			User user = userRepository.find(userId);
			Context context = contextRepository.find(contextId);

			if (user != null && context != null) {
				ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(context.getId(),
						user.getId());
				if (contextUserAuthentication != null) {
					contextUserAuthRepository.delete(contextUserAuthentication);
					return new ResponseEntity<ContextUserAuthentication>(contextUserAuthentication, HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_user", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function updates the current user context or sets new one if the user was not logged in
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/context/{userId}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")

	public ResponseEntity<CurrentContext> selectUserContext(@PathVariable("userId") String userId, @RequestBody Context context,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(userId) && context != null) {
			ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(context.getId(), userId);

			if (contextUserAuthentication != null) {
				CurrentContext currentContext = currentContextRepository.findByUserId(userId);

				if (currentContext != null) {
					currentContext.setContextUserAuthenticationId(contextUserAuthentication.getId());
					currentContextRepository.update(currentContext);
				} else {
					currentContext = new CurrentContext(userId, contextUserAuthentication.getId());
					currentContextRepository.save(currentContext);
				}

				return new ResponseEntity<CurrentContext>(currentContext, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all context for the provided user
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/context/{userId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<ContextDTO>> getContextsByUserId(@PathVariable("userId") String userId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(userId)) {
			List<ContextUserAuthentication> contextUserAuthList = contextUserAuthRepository.getByUserId(userId);
			if (contextUserAuthList != null) {
				List<ContextDTO> contextList = new ArrayList<>();

				for (ContextUserAuthentication contextUserAuth : contextUserAuthList) {
					Context context = contextRepository.find(contextUserAuth.getContextId());
					if (context != null) {
						ContextDTO contextDTO = new ContextDTO(context, contextUserAuth.getUserRole());
						contextList.add(contextDTO);
					}
				}
				return new ResponseEntity<List<ContextDTO>>(contextList, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function adds new context (This is only possible for admins or superadmins)
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/context/add/{userId}/{contextId}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")

	public ResponseEntity<Context> addContext(@PathVariable("userId") String userId, @PathVariable("contextId") String contextId,
			@RequestBody Context context, @RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(contextId) && context != null) {

			// Update context
			if (!Strings.isNullOrEmpty(context.getId())) {
				contextRepository.update(context);
				return new ResponseEntity<Context>(context, HttpStatus.OK);
			} else {
				// Add new context
				if (!userUtils.checkIfContextAlreadyExists(context, userId)) {
					Context currentContext = contextRepository.find(contextId);
					User currentUser = userRepository.find(userId);
					ContextUserAuthentication contextUserAuth = contextUserAuthRepository.getByContextIdAndUserId(contextId, userId);
					if (currentContext != null && currentUser != null && contextUserAuth != null) {
						String licensePlanName = "Default";
						LicensePlan licensePlan = licensePlanRepository.findByName(licensePlanName);
						if (licensePlan != null) {

							context.setLicensePlanId(licensePlan.getId());
							ObjectId savedContextId = contextRepository.saveAndReturnId(context);

							if (savedContextId != null) {
								ContextUserAuthentication contextUserAuthenticationNew = new ContextUserAuthentication(userId, savedContextId.toString(),
										contextUserAuth.getUserRole());
								contextUserAuthRepository.save(contextUserAuthenticationNew);

								// add standard group for current context
								dataInitialization.addDefaultGroup(userId, savedContextId.toString());
								return new ResponseEntity<Context>(context, HttpStatus.OK);
							}
						}
					}
				} else {
					log.error("Context {} already exist", context.getName());
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_context_exists", locale)),
							HttpStatus.NOT_FOUND);
				}
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function deletes the selected Context
	 *
	 * @param userId
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/deleteContext/{contextId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Context> deleteContext(@PathVariable("contextId") String contextId, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(contextId)) {
			Context context = contextRepository.find(contextId);
			if (context != null) {
				List<ContextUserAuthentication> contextUserAuthList = contextUserAuthRepository.getByContextId(context.getId());
				if (contextUserAuthList != null) {
					for (ContextUserAuthentication contextUserAuth : contextUserAuthList) {
						currentContextRepository.deleteByContextUserAuthenticationId(contextUserAuth.getId());
					}
				}
				contextRepository.deleteByContextId(contextId);
				contextUserAuthRepository.deleteByContextId(contextId);
				return new ResponseEntity<Context>(context, HttpStatus.OK);
			}

		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_user", locale)),
				HttpStatus.NOT_FOUND);
	}
}
