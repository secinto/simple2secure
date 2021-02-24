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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.simple2secure.api.dto.UserDTO;
import com.simple2secure.api.dto.UserInvitationDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.TokenObject;
import com.simple2secure.api.model.UserInvitation;
import com.simple2secure.api.model.UserInvitationRequest;
import com.simple2secure.api.model.UserInvitationStatus;
import com.simple2secure.api.model.UserLogin;
import com.simple2secure.api.model.UserRegistration;
import com.simple2secure.api.model.UserRegistrationResponse;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputDevice;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputToken;
import com.simple2secure.portal.validation.model.ValidInputUser;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.NotSecuredApi;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.USER_API)
@Slf4j
public class UserController extends BaseUtilsProvider {

	@Value("${keycloak.realm}")
	private String realm;

	/**
	 * This function finds and returns user according to the user id
	 */
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@ValidRequestMapping(
			method = ValidRequestMethodType.GET)
	public ResponseEntity<UserDTO> getUserByID(@ServerProvidedValue ValidInputUser userId, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (contextId.getValue() != null) {
			// Retrieving the context according to the current active context
			Context context = contextRepository.find(contextId.getValue());

			if (context != null) {

				// Retrieving the current UserContextAuth
				ContextUserAuthentication contextUserAuth = contextUserAuthRepository.getByContextIdAndUserId(context.getId(), userId.getValue());

				List<ObjectId> assignedGroups = new ArrayList<>();
				List<CompanyGroup> groups = groupUtils.getAllGroupsByContextId(context);
				List<Context> myContexts = contextUtils.getContextsByUserId(userId.getValue());
				log.debug("Found {} groups", groups.size());
				if (contextUserAuth != null) {
					if (contextUserAuth.getUserRole().equals(UserRole.SUPERUSER)) {
						assignedGroups = groupUtils.getAllAssignedGroupIdsForSuperUser(context, userId.getValue());
					}
				}

				UserDTO userDTO = new UserDTO(groups, myContexts, assignedGroups);
				return new ResponseEntity<>(userDTO, HttpStatus.OK);
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_user_not_found", locale.getValue()));
	}

	/**
	 * This function invites the user into the context. If the user is not registered, an email invitation should be sent. For each
	 * invitation, object with the invitation details will be saved in the invitation repository. This object will be used by the invited user
	 * in order to accept the invitation or reject it.
	 */
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@ValidRequestMapping(
			value = "/inviteContext",
			method = ValidRequestMethodType.POST)

	public ResponseEntity<UserInvitation> inviteUserToContext(@ServerProvidedValue ValidInputUser userId,
			@ServerProvidedValue ValidInputContext contextId, @RequestBody UserInvitationRequest userInvitationRequest,
			@ServerProvidedValue ValidInputLocale locale) {

		if (contextId.getValue() != null && userInvitationRequest != null) {

			// Retrieving the context according to the current active context
			Context context = contextRepository.find(contextId.getValue());

			if (context != null) {

				UsersResource usersResource = dataInitialization.keycloak.realm(realm).users();
				List<UserRepresentation> users = usersResource.search(userInvitationRequest.getEmail());

				// Check if user is registered (there can be only one user with the provided
				if (users != null && users.size() > 0) {

					// Invite/add user to this context
					String invitation_userId = users.get(0).getId();
					if (!Strings.isNullOrEmpty(invitation_userId)) {
						UserInvitation userInvitation = new UserInvitation(invitation_userId, contextId.getValue(), userInvitationRequest.getUserRole(),
								userId.getValue(), UserInvitationStatus.PENDING);
						if (userUtils.addUserInvitation(userInvitation)) {
							return new ResponseEntity<>(userInvitation, HttpStatus.OK);
						} else {
							throw new ApiRequestException(messageByLocaleService.getMessage("user_invitation_already_in_context", locale.getValue()));
						}

					}
				} else {
					// TODO: if user is not registered, send him an invitation using email and
					// notify user, currently notifying user that provided
					// user is not registered
					throw new ApiRequestException(messageByLocaleService.getMessage("user_invitation_not_registered", locale.getValue()));
				}

			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_user_not_found", locale.getValue()));
	}

	/**
	 * This function returns the user context invitations according to the userId, if some are available
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@ValidRequestMapping(
			value = "/acceptInvitation",
			method = ValidRequestMethodType.POST)

	public ResponseEntity<UserInvitation> acceptOrDeclineInvitation(@ServerProvidedValue ValidInputUser userId,
			@RequestBody UserInvitationDTO userInvitation, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(userId.getValue()) && userInvitation != null) {
			// Retrieving the context according to the current active context
			if (userInvitation.isAccepted()) {
				ContextUserAuthentication contextUserAuth = new ContextUserAuthentication(userId.getValue(),
						userInvitation.getInvitation().getContextId(), userInvitation.getInvitation().getUserRole(), false);
				contextUserAuthRepository.save(contextUserAuth);
				userInvitation.getInvitation().setInvitationStatus(UserInvitationStatus.ACCEPTED);
				userInvitationRepository.update(userInvitation.getInvitation());
			} else {
				userInvitationRepository.update(userInvitation.getInvitation());
			}
			return new ResponseEntity<>(userInvitation.getInvitation(), HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_user_not_found", locale.getValue()));
	}

	/**
	 * This function returns the user context invitations according to the userId, if some are available
	 */
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@ValidRequestMapping(
			value = "/invitations",
			method = ValidRequestMethodType.GET)
	public ResponseEntity<List<UserInvitationDTO>> getUserInvitationsByUserId(@ServerProvidedValue ValidInputUser userId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(userId.getValue())) {
			// Retrieving the context according to the current active context
			List<UserInvitation> invitations = userInvitationRepository.getByUserIdAndStatus(userId.getValue(), UserInvitationStatus.PENDING);

			if (invitations != null) {
				List<UserInvitationDTO> invitationsDTO = new ArrayList<>();

				for (UserInvitation invitation : invitations) {
					Context context = contextRepository.find(invitation.getContextId());
					UserInvitationDTO invitationDTO = new UserInvitationDTO(invitation, context.getName(), invitation.getInvitedByUserId(), false);
					invitationsDTO.add(invitationDTO);
				}

				return new ResponseEntity<>(invitationsDTO, HttpStatus.OK);
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_user_not_found", locale.getValue()));
	}

	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@ValidRequestMapping(
			value = "/isAdminForDevice",
			method = ValidRequestMethodType.GET)
	public ResponseEntity<Boolean> isAdminForDevice(@PathVariable ValidInputDevice deviceId, @ServerProvidedValue ValidInputUser userId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (userId != null) {
			CompanyLicensePublic license = licenseRepository.findByDeviceId(deviceId.getValue());
			CompanyGroup group = groupRepository.find(license.getGroupId());
			ContextUserAuthentication contextUA = contextUserAuthRepository.getByContextIdAndUserId(group.getContextId(), userId.getValue());
			if (contextUA != null) {
				if (contextUA.getUserRole().equals(UserRole.ADMIN) || contextUA.getUserRole().equals(UserRole.SUPERADMIN)) {
					return new ResponseEntity<>(true, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(false, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<>(false, HttpStatus.OK);
			}
		}
		// TODO: Check this again what the response is
		throw new ApiRequestException(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue()));
	}

	// Unsecured APIs

	/**
	 * This function only redirects the user to the correct page in the web for accepting the user invitation.
	 *
	 * @param user
	 * @return
	 * @throws URISyntaxException
	 */
	@NotSecuredApi
	@ValidRequestMapping(
			value = "/invite")
	public ResponseEntity<String> showAcceptInvitationPage(@PathVariable ValidInputToken token, @ServerProvidedValue ValidInputLocale locale)
			throws URISyntaxException {

		// TODO - check if token exists
		URI url = new URI(loadedConfigItems.getBaseURLWeb() + "/#/invitation/" + token.getValue());
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(url);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}

	@NotSecuredApi
	@ValidRequestMapping(
			value = "/login",
			method = ValidRequestMethodType.POST)
	public ResponseEntity<TokenObject> login(@RequestBody UserLogin userLogin, @ServerProvidedValue ValidInputLocale locale) {
		if (userLogin != null) {
			return userUtils.processLogin(userLogin, locale);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue()));
	}

	@NotSecuredApi
	@ValidRequestMapping(
			value = "/register",
			method = ValidRequestMethodType.POST)
	public ResponseEntity<UserRegistrationResponse> register(@RequestBody UserRegistration userRegistration,
			@ServerProvidedValue ValidInputLocale locale) {
		if (userRegistration != null) {
			return userUtils.processRegistration(userRegistration, locale);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue()));
	}

	@NotSecuredApi
	@ValidRequestMapping(
			value = "/forgotPassword",
			method = ValidRequestMethodType.POST)
	public ResponseEntity<UserRegistrationResponse> forgotPassword(@RequestBody UserRegistration userRegistration,
			@ServerProvidedValue ValidInputLocale locale) {
		if (userRegistration != null) {
			return userUtils.processForgotPassword(userRegistration, locale);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue()));
	}

	@NotSecuredApi
	@ValidRequestMapping(
			value = "/logout",
			method = ValidRequestMethodType.POST)
	public ResponseEntity<String> logout(@RequestBody TokenObject tokenObj, @ServerProvidedValue ValidInputLocale locale) {
		if (tokenObj != null) {

			String body = portalUtils.decodeJWTAndReturnBody(tokenObj.getToken());

			if (!Strings.isNullOrEmpty(body)) {

				JsonNode json = JSONUtils.fromString(body);
				return userUtils.processLogout(json.get("sub").asText(), locale);

			}

		}
		throw new ApiRequestException(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue()));
	}
}
