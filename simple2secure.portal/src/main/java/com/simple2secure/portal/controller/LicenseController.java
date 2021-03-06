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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.AuthToken;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.model.LicenseActivation;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputGroup;
import com.simple2secure.portal.validation.model.ValidInputLocale;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.NotSecuredApi;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.LICENSE_API)
@Slf4j
public class LicenseController extends BaseUtilsProvider {

	@Value("${license.filepath}")
	private String licenseFilePath;

	@Value("${license.privateKey}")
	private String privateKeyPath;

	@Value("${license.publicKey}")
	private String publicKeyPath;

	@PostConstruct
	public void initialize() {
		licenseFilePath = LicenseUtil.getLicensePath(licenseFilePath);
		publicKeyPath = LicenseUtil.getLicenseKeyPath(publicKeyPath, licenseFilePath);
		privateKeyPath = LicenseUtil.getLicenseKeyPath(privateKeyPath, licenseFilePath);
		if (!Strings.isNullOrEmpty(publicKeyPath) && !Strings.isNullOrEmpty(privateKeyPath)) {
			LicenseUtil.initialize(licenseFilePath, privateKeyPath, publicKeyPath);
		} else {
			LicenseUtil.initialize(licenseFilePath);
		}
	}

	/*
	 * TODO: Check if it is enough to have only one token generated and if the current license structure and process is secure. Because anyone
	 * with the license can activate a device and provide data. Thus, we need to verify that this is the best way to do so.
	 */

	/**
	 * This function is used to activate POD or PROBE with the provided license. If it can be used to activate any additional device a new
	 * access token is generated and returned to the device.
	 *
	 * @param groupId
	 * @param licenseId
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws UnsupportedEncodingException
	 */
	@NotSecuredApi
	@ValidRequestMapping(
			value = "/authenticate",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompanyLicensePublic> authenticate(@RequestBody CompanyLicensePublic licensePublic,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException, UnsupportedEncodingException {
		if (licensePublic != null) {

			if (licensePublic.getDeviceId() == null) {
				log.warn("License with or without pod and probe Id provided for checking token. This should usually not happen");
				throw new ApiRequestException(messageByLocaleService.getMessage("problem_during_activation", locale.getValue()));
			}

			LicenseActivation activation = null;

			activation = licenseUtils.authenticateLicense(licensePublic, locale.getValue());

			if (activation.isSuccess()) {
				licensePublic.setAccessToken(activation.getAccessToken());
				licensePublic.setRefreshToken(activation.getRefreshToken());
				licensePublic.setActivated(true);
				return new ResponseEntity<>(licensePublic, HttpStatus.OK);
			} else {
				throw new ApiRequestException(messageByLocaleService.getMessage(activation.getMessage(), locale.getValue()));
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_during_activation", locale.getValue()));
	}

	@NotSecuredApi
	@ValidRequestMapping(
			value = "/renewAuthentication",
			method = ValidRequestMethodType.POST)
	public ResponseEntity<CompanyLicensePublic> renewAuthentication(@RequestBody CompanyLicensePublic license,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException, UnsupportedEncodingException {
		if (license != null) {

			CompanyLicensePrivate privateLicense = licenseRepository.findByDeviceId(license.getDeviceId());

			if (privateLicense != null) {
				AuthToken accessToken = keycloakAuthenticationService.getNewTokenFromRefresh(license.getRefreshToken());

				if (accessToken != null) {
					privateLicense.setAccessToken(accessToken.getAuthToken());
					privateLicense.setRefreshToken(accessToken.getRefreshToken());

					licenseRepository.update(privateLicense);

					license.setAccessToken(accessToken.getAuthToken());
					license.setRefreshToken(accessToken.getRefreshToken());

					return new ResponseEntity<>(license, HttpStatus.OK);
				}
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_during_token_renewal", locale.getValue()));
	}

	/**
	 * Obtains the license for the provided group and the associated license plan. Returns the license contained in a ZIP file together with
	 * the public key for verification of the signature. The ZIP is returned as byte array. It is used in the WEB to provide a license
	 * download.
	 *
	 * @param groupId
	 * @param userId
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<byte[]> getLicense(@PathVariable ValidInputGroup groupId, @ServerProvidedValue ValidInputLocale locale)
			throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		httpHeaders.setContentDispositionFormData("attachment", "license.zip");

		CompanyGroup group = groupRepository.find(groupId.getValue());
		if (group != null) {
			ByteArrayOutputStream byteArrayOutputStream = licenseUtils.generateLicenseForPackage(group.getContextId(), groupId.getValue());
			return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("max_license_number_exceeded", locale.getValue()));
	}

	/**
	 * This function automatically downloads the license for the script
	 *
	 * TODO: Check if this works with keycloak
	 *
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@ValidRequestMapping(
			value = "/downloadLicenseForScript",
			method = ValidRequestMethodType.POST)
	public ResponseEntity<byte[]> logindAndDownload(@RequestBody String authToken, @ServerProvidedValue ValidInputLocale locale)
			throws Exception {
		if (!Strings.isNullOrEmpty(authToken)) {

			String payload = licenseUtils.getPayloadFromTheToken(authToken);
			String userID = licenseUtils.getFieldFromPayload(payload, "userID");

			if (userID != null) {
				List<ContextUserAuthentication> user_contexts = contextUserRepository.getByUserId(userID);
				if (user_contexts != null) {
					for (ContextUserAuthentication context : user_contexts) {
						if (context.isOwnContext()) {
							CompanyGroup group = groupRepository.findStandardGroupByContextId(context.getContextId());
							if (group != null) {
								return getLicense(new ValidInputGroup(group.getId().toHexString()), locale);
							}
						}
					}
				}
			}
		}

		return null;
	}

}
