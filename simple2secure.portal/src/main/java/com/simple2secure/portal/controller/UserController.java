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
package com.simple2secure.portal.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.UserDTO;
import com.simple2secure.api.dto.UserRoleDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserInfo;
import com.simple2secure.api.model.UserInvitation;
import com.simple2secure.api.model.UserRegistration;
import com.simple2secure.api.model.UserRegistrationType;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputToken;
import com.simple2secure.portal.validation.model.ValidInputUser;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.USER_API)
public class UserController extends BaseUtilsProvider {

	static final Logger log = LoggerFactory.getLogger(UserController.class);

	/**
	 * This function finds and returns user according to the user id
	 */
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@ValidRequestMapping(method = ValidRequestMethodType.GET)
	public ResponseEntity<UserDTO> getUserByID(@ServerProvidedValue ValidInputUser userId, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {

		User user = userRepository.find(userId.getValue());

		if (user != null && !Strings.isNullOrEmpty(contextId.getValue())) {
			// Retrieving the context according to the current active context
			Context context = contextRepository.find(contextId.getValue());

			if (context != null) {

				// Retrieving the current UserContextAuth
				ContextUserAuthentication contextUserAuth = contextUserAuthRepository.getByContextIdAndUserId(context.getId(), user.getId());

				List<String> assignedGroups = new ArrayList<>();
				List<CompanyGroup> groups = groupUtils.getAllGroupsByContextId(context);
				List<UserRoleDTO> myUsers = userUtils.getAllUsersFromCurrentContext(context, user.getId());
				List<Context> myContexts = contextUtils.getContextsByUserId(user);
				UserInfo userInfo = userInfoRepository.getByUserId(user.getId());
				log.debug("Found {} groups, {} users, and {} contexts", groups.size(), myUsers.size(), myContexts.size());
				if (contextUserAuth != null) {
					if (contextUserAuth.getUserRole().equals(UserRole.SUPERUSER)) {
						assignedGroups = groupUtils.getAllAssignedGroupIdsForSuperUser(context, user);
					}
				}

				UserDTO userDTO = new UserDTO(userInfo, myUsers, groups, myContexts, assignedGroups);
				return new ResponseEntity<>(userDTO, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function is used to add new user. For each user added it sets the configuration, queries, processors and steps from the default
	 * configuration files which are placed on the server. This function is called from a super user or admin from within the portal. If the
	 * user is added by the normal registration process this is performed via {@link #registerUser(UserRegistration, String)}.
	 *
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	@ValidRequestMapping(method = ValidRequestMethodType.POST)
	public ResponseEntity<User> insertUser(@RequestBody UserRegistration user, @ServerProvidedValue ValidInputUser userId,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException, IOException, URISyntaxException {

		if (user != null) {
			if (user.getRegistrationType().equals(UserRegistrationType.ADDED_BY_USER)) {
				user.setCurrentContextId(contextId.getValue());
				user.setAddedByUserId(userId.getValue());

				return userUtils.addNewUser(user, locale.getValue());
			} else {
				return userUtils.updateUser(user, locale.getValue());
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale.getValue())),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This function is used for adding users via the standard registration process provided on the login page.
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@ValidRequestMapping(value = "/register", method = ValidRequestMethodType.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> registerUser(@RequestBody UserRegistration user, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException, IOException, URISyntaxException {
		if (user != null) {
			return userUtils.addNewUser(user, locale.getValue());
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale.getValue())),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This function is used for adding users via the standard registration process provided on the login page.
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@ValidRequestMapping(value = "/resendActivation", method = ValidRequestMethodType.POST)
	public ResponseEntity<User> resendActivation(@RequestBody String email, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException, IOException, URISyntaxException {
		if (!Strings.isNullOrEmpty(email)) {
			User user = userRepository.findByEmail(email);
			if (user != null) {
				if (!user.isActivated()) {
					return userUtils.resendActivation(user, locale.getValue());
				} else {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("user_already_activated", locale.getValue())),
							HttpStatus.NOT_FOUND);
				}
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This user updates user info
	 *
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@ValidRequestMapping(value = "/update", method = ValidRequestMethodType.POST)
	public ResponseEntity<UserInfo> updateUserInfo(@RequestBody UserInfo userInfo, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException, IOException, URISyntaxException {

		if (userInfo != null) {
			User user = userRepository.find(userInfo.getUserId());

			if (user != null) {
				userInfoRepository.update(userInfo);
				return new ResponseEntity<>(userInfo, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale.getValue())),
				HttpStatus.NOT_FOUND);
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
	@ValidRequestMapping(value = "/activate/updatePassword", method = ValidRequestMethodType.POST)
	public ResponseEntity<User> updateUserPasswordFirstLogin(@PathVariable ValidInputToken token, @RequestBody String password,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException, URISyntaxException, IOException {

		if (!Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(token.getValue())) {
			User user = userRepository.findByActivationToken(token.getValue());

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
				return new ResponseEntity(
						new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale.getValue())),
						HttpStatus.NOT_FOUND);
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue())),
					HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * This function activates the user, after the user has clicked on the link in the activation email which has been sent after successful
	 * registration
	 *
	 * @param token
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@ValidRequestMapping(value = "/activate", method = ValidRequestMethodType.GET)
	public ResponseEntity<InputStreamResource> activateUser(@PathVariable ValidInputToken token, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException, URISyntaxException, IOException {
		User user = userRepository.findByActivationToken(token.getValue());

		if (user != null) {

			if (!user.isActivated()) {
				if (user.isPasswordUpdated()) {
					user.setActivated(true);
					userRepository.update(user);
				}

				URI url = new URI(loadedConfigItems.getBaseURLWeb() + "/#/account/activate/" + token.getValue());

				if (!user.isPasswordUpdated()) {
					url = new URI(loadedConfigItems.getBaseURLWeb() + "/#/account/updatePassword/" + token.getValue());
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
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("activation_token_not_valid", locale.getValue())),
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
	@ValidRequestMapping(value = "/sendResetPasswordEmail", method = ValidRequestMethodType.POST)
	public ResponseEntity<User> sendResetPasswordEmail(@RequestBody String email, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException, IOException {

		if (!Strings.isNullOrEmpty(email)) {
			User user = userRepository.findByEmailOnlyActivated(email);
			if (user != null) {
				user.setPasswordResetToken(portalUtils.generateToken());
				user.setPasswordResetExpirationTime(System.currentTimeMillis() + StaticConfigItems.expiration_time_password_reset);

				userRepository.update(user);

				// Add template for the email content and retrieve it from the database!
				String emailContent = "In order to change your password please click on the link below. \n\n Password reset link: "
						+ loadedConfigItems.getBaseURL() + "/api/user/resetPassword/" + user.getPasswordResetToken();

				if (mailUtils.sendEmail(user, emailContent, StaticConfigItems.email_subject_pr)) {
					return new ResponseEntity<>(user, HttpStatus.OK);
				} else {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_sending_email", locale.getValue())),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity(
						new CustomErrorType(messageByLocaleService.getMessage("user_with_provided_email_not_exists", locale.getValue())),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue())),
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

	@ValidRequestMapping(value = "/resetPassword")
	@PermitAll
	public ResponseEntity<User> redirectToChangePasswordPage(@PathVariable ValidInputToken token) throws URISyntaxException {
		URI url = new URI(loadedConfigItems.getBaseURLWeb() + "/#/resetPassword/" + token.getValue());
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(url);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}

	/**
	 * This function only redirects the user to the correct page in the web for accepting the user invitation.
	 *
	 * @param user
	 * @return
	 * @throws URISyntaxException
	 */
	@ValidRequestMapping(value = "/invite")
	public ResponseEntity<User> showAcceptInvitationPage(@PathVariable ValidInputToken token, @ServerProvidedValue ValidInputLocale locale)
			throws URISyntaxException {

		// TODO - check if token exists
		URI url = new URI(loadedConfigItems.getBaseURLWeb() + "/#/invitation/" + token.getValue());
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(url);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}

	/**
	 * @throws ItemNotFoundRepositoryException
	 *
	 */
	@ValidRequestMapping(value = "/invite/process")
	public ResponseEntity<UserInvitation> processInvitation(@PathVariable String token, @RequestParam boolean isAccepted,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(token)) {
			UserInvitation userInvitation = userInvitationRepository.getByInvitationToken(token);
			if (userInvitation != null) {
				User user = userRepository.find(userInvitation.getUserId());
				if (isAccepted && user != null) {
					if (portalUtils.checkIfTokenIsStillValid(userInvitation.getInvitationTokenExpirationTime())) {
						Context context = contextRepository.find(userInvitation.getContextId());
						if (context != null) {
							ContextUserAuthentication contextUserAuth = new ContextUserAuthentication(userInvitation.getUserId(),
									userInvitation.getContextId(), userInvitation.getUserRole(), false);
							// If user role is superuser add groups
							if (userInvitation.getUserRole().equals(UserRole.SUPERUSER)) {
								if (userInvitation.getGroupIds() != null) {
									groupUtils.updateGroupAccessRightsforTheSuperuser(userInvitation.getGroupIds(), user, context);
								}
							}
							contextUserAuthRepository.save(contextUserAuth);
							userInvitationRepository.delete(userInvitation);
							return new ResponseEntity<>(userInvitation, HttpStatus.OK);

						} else {
							userInvitationRepository.delete(userInvitation);
							log.error("Context not found for following {}", token);
						}
					} else {
						log.error("Invitation token expired {}", token);
						userInvitationRepository.delete(userInvitation);
						return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("invitation_token_expired", locale.getValue())),
								HttpStatus.NOT_FOUND);
					}

				} else {
					userInvitationRepository.delete(userInvitation);
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("invitation_rejected", locale.getValue())),
							HttpStatus.NOT_FOUND);
				}

			}
		}
		log.error("Error occured during invitation for invitation token {}", token);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue())),
				HttpStatus.NOT_FOUND);
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
	@ValidRequestMapping(value = "/updatePassword", method = ValidRequestMethodType.POST)
	public ResponseEntity<User> updateUserPassword(@PathVariable ValidInputToken token, @RequestBody String password,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException, URISyntaxException, IOException {

		if (!Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(token.getValue())) {
			User user = userRepository.findByPasswordResetToken(token.getValue());

			if (user != null) {
				if (portalUtils.checkIfTokenIsStillValid(user.getPasswordResetExpirationTime())) {

					user.setPassword(password);

					String error = userUtils.validateUserPassword(user);

					if (!Strings.isNullOrEmpty(error)) {
						return new ResponseEntity(new CustomErrorType(error), HttpStatus.NOT_FOUND);
					}

					user.setPassword(passwordEncoder.encode(password));
					user.setPasswordResetToken(token.getValue());
					userRepository.update(user);

					String emailContent = messageByLocaleService.getMessage("password_changed_email_content", locale.getValue());
					mailUtils.sendEmail(user, emailContent, StaticConfigItems.email_subjct_pcs);

					URI url = new URI(loadedConfigItems.getBaseURLWeb());
					HttpHeaders httpHeaders = new HttpHeaders();
					httpHeaders.setLocation(url);
					return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
				} else {
					return new ResponseEntity(
							new CustomErrorType(messageByLocaleService.getMessage("password_reset_token_expired", locale.getValue())),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_token_already_used", locale.getValue())),
						HttpStatus.NOT_FOUND);
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue())),
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
	@ValidRequestMapping(method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<ContextUserAuthentication> deleteUser(@PathVariable String userId, @PathVariable String contextId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		// TODO: Define it so that user with ADMIN or SUPERADMIN must have at least one context???
		if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(contextId)) {

			User user = userRepository.find(userId);
			Context context = contextRepository.find(contextId);

			if (user != null && context != null) {
				ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(context.getId(),
						user.getId());
				if (contextUserAuthentication != null) {
					if (contextUserAuthentication.getUserRole().equals(UserRole.SUPERADMIN)) {
						return new ResponseEntity(
								new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_superadmin", locale.getValue())),
								HttpStatus.NOT_FOUND);
					} else {
						contextUtils.deleteContextAuthDependencies(contextUserAuthentication);
						return new ResponseEntity<>(contextUserAuthentication, HttpStatus.OK);
					}
				}
			}
		}
		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_user", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

}
