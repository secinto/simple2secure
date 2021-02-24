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
package com.simple2secure.portal.security.auth;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.simple2secure.api.model.AuthToken;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.CurrentContextRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.utils.DataInitialization;
import com.simple2secure.portal.utils.PortalUtils;

@Service
public class DeviceAuthenticationService {

	public static final Logger log = LoggerFactory.getLogger(DeviceAuthenticationService.class);

	@Autowired
	SettingsRepository settingsRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	CurrentContextRepository currentContextRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	DataInitialization dataInitialization;

	@Autowired
	KeycloakAuthenticationService keycloakAuthenticationService;

	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;

	@Value("${keycloak.resource}")
	private String resource;

	/**
	 * <<<<<<< simple2secure.portal/src/main/java/com/simple2secure/portal/security/auth/DeviceAuthenticationService.java This function is
	 * used to create probe authentication token so that it is available to send data to the portal ======= This function is used to create
	 * probe and pod authentication token so that it is available to send data to the portal >>>>>>>
	 * simple2secure.portal/src/main/java/com/simple2secure/portal/security/auth/DeviceAuthenticationService.java
	 *
	 * @param deviceId
	 * @param group
	 * @param license
	 * @return
	 */
	public AuthToken addDeviceAuthentication(ObjectId deviceId, CompanyGroup group, CompanyLicensePrivate license) {
		if (deviceId != null && group != null && license != null) {

			RealmResource realmResource = dataInitialization.keycloak.realm(StaticConfigItems.REALM_S2S_DEVELOPMENT);
			UsersResource usersResource = realmResource.users();

			String email = deviceId + "@secinto.com";
			List<UserRepresentation> users = usersResource.search(deviceId.toHexString());

			// If there is no user in keycloak associated with license id, create new one
			if (users == null || users.isEmpty()) {

				UserRepresentation user = new UserRepresentation();
				user.setEnabled(true);
				user.setEmail(email);
				user.setUsername(deviceId.toHexString());

				Response response = usersResource.create(user);
				String userId = CreatedResponseUtil.getCreatedId(response);

				CredentialRepresentation credential = new CredentialRepresentation();
				credential.setType(CredentialRepresentation.PASSWORD);
				credential.setValue(license.getTokenSecret());
				credential.setTemporary(false);

				UserResource userResource = usersResource.get(userId);

				// Set password credential
				userResource.resetPassword(credential);

				// Set Device role
				RoleRepresentation deviceRole = realmResource.roles().get("DEVICE").toRepresentation();

				userResource.roles().realmLevel().add(Arrays.asList(deviceRole));
			}

			// keycloak login to retrieve authToken
			AuthToken authToken = keycloakAuthenticationService.getNewTokenFromCredentials(email, license.getTokenSecret());

			return authToken;

		} else {
			log.error("Probe id or group is null");
			return null;
		}

	}
}
