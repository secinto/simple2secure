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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.validation.ValidInputGroup;
import com.simple2secure.api.model.validation.ValidInputLocale;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.license.LicenseDateUtil;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.model.LicenseActivation;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TokenRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.security.auth.TokenAuthenticationService;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.DataInitialization;
import com.simple2secure.portal.utils.LicenseUtils;
import com.simple2secure.portal.utils.PortalUtils;
import com.simple2secure.portal.utils.SUTUtils;
import com.simple2secure.portal.validator.ValidInput;
import com.simple2secure.portal.validator.ValidRequestMapping;

@RestController
@RequestMapping(StaticConfigItems.LICENSE_API)
public class LicenseController {
	private static Logger log = LoggerFactory.getLogger(LicenseController.class);

	@Value("${license.filepath}")
	private String licenseFilePath;

	@Value("${license.privateKey}")
	private String privateKeyPath;

	@Value("${license.publicKey}")
	private String publicKeyPath;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	SettingsRepository settingsRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	LicensePlanRepository licensePlanRepository;

	@Autowired
	TestRepository testRepository;

	@Autowired
	DataInitialization dataInitialization;

	@Autowired
	TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ContextUserAuthRepository contextUserRepository;

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	LicenseUtils licenseUtils;

	@Autowired
	SUTUtils sutUtils;

	@Autowired
	RestTemplate restTemplate;

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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ValidRequestMapping(value = "/authenticate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompanyLicensePublic> activate(@RequestBody CompanyLicensePublic licensePublic, @ValidInput ValidInputLocale locale)
			throws ItemNotFoundRepositoryException, UnsupportedEncodingException {
		if (licensePublic != null) {

			boolean podAuthentication = false;
			if (!Strings.isNullOrEmpty(licensePublic.getDeviceId()) && licensePublic.isDevicePod()) {
				podAuthentication = true;
			} else if (!Strings.isNullOrEmpty(licensePublic.getDeviceId()) && !licensePublic.isDevicePod()) {
				podAuthentication = false;
			} else {
				log.warn("License with or without pod and probe Id provided for checking token. This should usually not happen");
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale.getValue())),
						HttpStatus.NOT_FOUND);
			}
			LicenseActivation activation = null;

			if (!licensePublic.isActivated()) {
				activation = licenseUtils.activateLicense(licensePublic, podAuthentication, locale.getValue());
				if (!licensePublic.isDevicePod()) {
					sutUtils.addProbeAsSUT(licensePublic);
				}
			} else {
				activation = licenseUtils.checkToken(licensePublic, podAuthentication, locale.getValue());
			}
			if (activation.isSuccess()) {
				CompanyLicensePrivate licensePrivate = licenseRepository.findByDeviceId(licensePublic.getDeviceId());
				if (licensePrivate != null) {
					licensePublic = licensePrivate.getPublicLicense();
				}

				return new ResponseEntity(licensePublic, HttpStatus.OK);
			} else {
				return new ResponseEntity(new CustomErrorType(activation.getMessage()), HttpStatus.NOT_FOUND);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * Obtains the license for the provided group and user for the associated license plan. Returns the license contained in a ZIP file
	 * together with the public key for verification of the signature. The ZIP is returned as byte array. It is used in the WEB to provide a
	 * license download.
	 *
	 * @param groupId
	 * @param userId
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<byte[]> getLicense(ValidInputGroup groupId, @ValidInput ValidInputLocale locale) throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		httpHeaders.setContentDispositionFormData("attachment", "license.zip");

		CompanyGroup group = groupRepository.find(groupId.getValue());
		Context context = contextRepository.find(group.getContextId());

		if (context != null) {
			LicensePlan licensePlan = licensePlanRepository.find(context.getLicensePlanId());
			if (licensePlan != null) {
				if (context.getCurrentNumberOfLicenseDownloads() < licensePlan.getMaxNumberOfDownloads()) {

					String expirationDate = LicenseDateUtil.getLicenseExpirationDate(licensePlan.getValidity(), licensePlan.getValidityUnit());
					/*
					 * TODO: Generates a new license for each request. Should not be the case
					 */
					List<CompanyLicensePrivate> companyLicenses = licenseRepository.findAllByGroupId(groupId.getValue());
					String licenseId = LicenseUtil.generateLicenseId();
					CompanyLicensePrivate companyLicense = new CompanyLicensePrivate(groupId.getValue(), licenseId, expirationDate, false);

					if (companyLicenses != null && companyLicenses.size() > 0) {
						licenseId = companyLicenses.get(companyLicenses.size() - 1).getLicenseId();
						companyLicense = new CompanyLicensePrivate(groupId.getValue(), licenseId, expirationDate, false);
					} else {
						licenseRepository.save(companyLicense);
					}

					String licenseFile = LicenseUtil.createLicenseFile(companyLicense.getGroupId(), companyLicense.getLicenseId(),
							companyLicense.getExpirationDate());

					ByteArrayOutputStream byteArrayOutputStream = LicenseUtil.generateLicenseZIPStreamFromFile(licenseFile,
							licenseFilePath + "public.key");

					context.setCurrentNumberOfLicenseDownloads(context.getCurrentNumberOfLicenseDownloads() + 1);
					contextRepository.update(context);

					return new ResponseEntity(byteArrayOutputStream.toByteArray(), HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("max_license_number_exceeded", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function deletes the user from the current context
	 *
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@ValidRequestMapping(value = "/downloadLicenseForScript", method = RequestMethod.POST)
	public ResponseEntity<byte[]> logindAndDownload(@RequestBody String authToken, @ValidInput ValidInputLocale locale) throws Exception {
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
								return getLicense(new ValidInputGroup(group.getId()), locale);
							}
						}
					}
				}
			}
		}

		return null;
	}

}
