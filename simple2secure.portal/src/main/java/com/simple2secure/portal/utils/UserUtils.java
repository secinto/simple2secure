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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.AuthToken;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.TokenObject;
import com.simple2secure.api.model.UserInvitation;
import com.simple2secure.api.model.UserLogin;
import com.simple2secure.api.model.UserRegistration;
import com.simple2secure.api.model.UserRegistrationResponse;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseServiceProvider;
import com.simple2secure.portal.validation.model.ValidInputLocale;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserUtils extends BaseServiceProvider {

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	MailUtils mailUtils;

	@Autowired
	GroupUtils groupUtils;

	@Autowired
	ContextUtils contextUtils;

	/**
	 * This function add new user invitation and saves it in the dedicated userInvitation table
	 *
	 * @param userInvitation
	 * @return
	 */
	public boolean addUserInvitation(UserInvitation userInvitation) {
		if (userInvitation != null) {
			// check if user is already part of this context
			ContextUserAuthentication contextUserAuth = contextUserAuthRepository.getByContextIdAndUserId(userInvitation.getContextId(),
					userInvitation.getUserId());
			// check if there is already user invitation
			UserInvitation currentUserInvitation = userInvitationRepository.getByContextIdAndUserId(userInvitation.getContextId(),
					userInvitation.getUserId());

			if (contextUserAuth == null && currentUserInvitation == null) {
				userInvitationRepository.save(userInvitation);
				return true;
			}
		}
		return false;
	}

	/**
	 * This function is used to process the user registration using keycloak
	 *
	 * @param userRegistration
	 * @param locale
	 * @return
	 */
	public ResponseEntity<UserRegistrationResponse> processRegistration(UserRegistration userRegistration, ValidInputLocale locale) {
		RealmResource realmResource = dataInitialization.keycloak.realm(StaticConfigItems.REALM_S2S_DEVELOPMENT);
		UsersResource usersResource = realmResource.users();
		List<UserRepresentation> users = usersResource.search(userRegistration.getEmail());

		if (users == null || users.isEmpty()) {
			UserRepresentation user = new UserRepresentation();
			user.setEnabled(true);
			user.setEmail(userRegistration.getEmail());
			user.setUsername(userRegistration.getEmail());
			List<String> requiredActions = new ArrayList<>();
			requiredActions.add("VERIFY_EMAIL");
			user.setRequiredActions(requiredActions);
			// user.singleAttribute("locale", locale.getValue());
			Response response = usersResource.create(user);
			String userId = CreatedResponseUtil.getCreatedId(response);

			CredentialRepresentation credential = new CredentialRepresentation();
			credential.setType(CredentialRepresentation.PASSWORD);
			credential.setValue(userRegistration.getPassword());
			credential.setTemporary(false);

			UserResource userResource = usersResource.get(userId);

			userResource.resetPassword(credential);

			userResource.executeActionsEmail(StaticConfigItems.CLIENT_S2S_WEB, StaticConfigItems.REDIRECT_URI, requiredActions);

			UserRegistrationResponse response_user = new UserRegistrationResponse(
					messageByLocaleService.getMessage("user_created_message", locale.getValue()));

			ObjectId contextId = contextUtils.addNewContextForRegistration(userRegistration.getEmail(), userId);

			if (contextId != null) {
				ObjectId contextUserAuth = contextUtils.addContextUserAuthentication(userId, contextId, UserRole.ADMIN, true);

				if (contextUserAuth != null) {
					groupUtils.createNewGroupRegistration(contextId);
				}
			}

			return new ResponseEntity<>(response_user, HttpStatus.OK);

		}
		throw new ApiRequestException(messageByLocaleService.getMessage("user_with_provided_email_already_exists", locale.getValue()));
	}

	/**
	 * This function is used to process the forget password using the keycloak
	 *
	 * @param userRegistration
	 * @param locale
	 * @return
	 */
	public ResponseEntity<UserRegistrationResponse> processForgotPassword(UserRegistration userRegistration, ValidInputLocale locale) {
		RealmResource realmResource = dataInitialization.keycloak.realm(StaticConfigItems.REALM_S2S_DEVELOPMENT);
		UsersResource usersResource = realmResource.users();
		List<UserRepresentation> users = usersResource.search(userRegistration.getEmail());

		if (users.size() == 1) {

			UserResource userResource = usersResource.get(users.get(0).getId());

			List<String> requiredActions = new ArrayList<>();
			requiredActions.add("UPDATE_PASSWORD");

			userResource.executeActionsEmail(StaticConfigItems.CLIENT_S2S_WEB, StaticConfigItems.REDIRECT_URI, requiredActions);
			UserRegistrationResponse response_user = new UserRegistrationResponse(
					messageByLocaleService.getMessage("user_reset_password", locale.getValue()));

			return new ResponseEntity<>(response_user, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("user_with_provided_email_not_exists", locale.getValue()));
	}

	/**
	 * This function is used to login user using the keycloak
	 *
	 * @param userLogin
	 * @param locale
	 * @return
	 */
	public ResponseEntity<TokenObject> processLogin(UserLogin userLogin, ValidInputLocale locale) {
		AuthToken token = keycloakAuthenticationService.getNewTokenFromCredentials(userLogin.getUsername(), userLogin.getPassword());

		if (token != null && !token.getAuthToken().isEmpty()) {
			TokenObject response = new TokenObject(token.getAuthToken());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("user_with_provided_creds_not_exists", locale.getValue()));
	}

	/**
	 * This function is used for logging out the user out the keycloak and removing the user session
	 *
	 * @param userId
	 * @param locale
	 * @return
	 */
	public ResponseEntity<String> processLogout(String userId, ValidInputLocale locale) {

		RealmResource realmResource = dataInitialization.keycloak.realm(StaticConfigItems.REALM_S2S_DEVELOPMENT);
		UsersResource usersResource = realmResource.users();

		if (usersResource != null) {
			UserResource userResource = usersResource.get(userId);

			if (userResource != null) {
				userResource.logout();
				return new ResponseEntity<>("Logged out successfully!", HttpStatus.OK);
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("user_with_provided_creds_not_exists", locale.getValue()));
	}

	/**
	 * This function checks if user is admin or superadmin in the certain context
	 *
	 * @param cUA
	 * @return
	 */
	public boolean checkIsUserAdminInContext(ContextUserAuthentication cUA) {
		if (cUA != null) {
			if (cUA.getUserRole().equals(UserRole.ADMIN) || cUA.getUserRole().equals(UserRole.SUPERADMIN)) {
				return true;
			}
		}
		return false;
	}
}
