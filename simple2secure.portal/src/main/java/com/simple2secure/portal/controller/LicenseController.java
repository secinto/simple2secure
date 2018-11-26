/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.simple2secure.api.model.AdminGroup;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.Settings;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.license.LicenseDateUtil;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.AdminGroupRepository;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.repository.TokenRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.security.auth.TokenAuthenticationService;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.DataInitialization;
import com.simple2secure.portal.utils.PortalUtils;

@RestController
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
	UserRepository userRepository;

	@Autowired
	ProcessorRepository processorRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	QueryRepository queryRepository;

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	SettingsRepository settingsRepository;

	@Autowired
	AdminGroupRepository adminGroupRepository;

	@Autowired
	LicensePlanRepository licensePlanRepository;

	@Autowired
	DataInitialization dataInitialization;

	@Autowired
	TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	RestTemplate restTemplate;

	@PostConstruct
	public void initialize() {
		licenseFilePath = LicenseUtil.getLicensePath(licenseFilePath);
		publicKeyPath = LicenseUtil.getLicenseKeyPath(publicKeyPath, licenseFilePath);
		privateKeyPath = LicenseUtil.getLicenseKeyPath(privateKeyPath, licenseFilePath);
		LicenseUtil.initialize(licenseFilePath, privateKeyPath, publicKeyPath);
	}

	/**
	 * This function is used to update the license in the mongodb and activate the probe when the license is imported for the first time
	 *
	 * @param groupId
	 * @param licenseId
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/license/activateProbe", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<String> activateLicense(@RequestBody CompanyLicensePublic licensePublic,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		if (licensePublic != null) {

			String groupId = licensePublic.getGroupId();
			String licenseId = licensePublic.getLicenseId();
			String probeId = licensePublic.getProbeId();

			if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(licenseId) && !Strings.isNullOrEmpty(probeId)) {
				CompanyGroup group = groupRepository.find(groupId);
				CompanyLicensePrivate license = licenseRepository.find(licenseId);

				if (group != null && license != null) {

					license.setTokenSecret(RandomStringUtils.randomAlphanumeric(20));
					String accessToken = tokenAuthenticationService.addLicenseAuthentication(probeId, group, license);

					if (!Strings.isNullOrEmpty(accessToken)) {

						license.setProbeId(probeId);
						license.setAccessToken(accessToken);
						license.setActivated(true);

						licenseRepository.update(license);

						return new ResponseEntity(accessToken, HttpStatus.OK);
					}
				}
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
				HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/api/license/{groupId}/{userId}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getLicense(@PathVariable("groupId") String groupId, @PathVariable("userId") String userId,
			@RequestHeader("Accept-Language") String locale) throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		httpHeaders.setContentDispositionFormData("attachment", "license.zip");

		CompanyGroup group = groupRepository.find(groupId);
		AdminGroup adminGroup = adminGroupRepository.find(group.getAdminGroupId());

		if (adminGroup != null) {
			LicensePlan licensePlan = licensePlanRepository.find(adminGroup.getLicensePlanId());
			if (licensePlan != null) {
				if (adminGroup.getCurrentNumberOfLicenseDownloads() < licensePlan.getMaxNumberOfDownloads()) {

					String expirationDate = LicenseDateUtil.getLicenseExpirationDate(licensePlan.getValidity(), licensePlan.getValidityUnit());

					CompanyLicensePrivate companyLicense = new CompanyLicensePrivate(groupId, LicenseUtil.generateLicenseId(), expirationDate, false);

					licenseRepository.save(companyLicense);

					LicenseUtil.createLicense(companyLicense.getGroupId(), companyLicense.getLicenseId(), companyLicense.getExpirationDate());

					ByteArrayOutputStream byteArrayOutputStream = LicenseUtil
							.generateLicenseZIPStream(StaticConfigItems.KEYS_LOCATION + "public.key");

					adminGroup.setCurrentNumberOfLicenseDownloads(adminGroup.getCurrentNumberOfLicenseDownloads() + 1);
					adminGroupRepository.update(adminGroup);

					return new ResponseEntity(byteArrayOutputStream.toByteArray(), HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("max_license_number_exceeded", locale)),
				HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/api/license/{licenseId}/{groupId}/{probeId}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> checkLicense(@PathVariable("licenseId") String licenseId, @PathVariable("groupId") String groupId,
			@PathVariable("probeId") String probeId, @RequestHeader("Accept-Language") String locale) throws Exception {
		CompanyLicensePrivate license = licenseRepository.find(licenseId);
		if (license != null && license.isActivated()) {
			if (license.getGroupId().equalsIgnoreCase(groupId) && license.getProbeId().equalsIgnoreCase(probeId)) {
				return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(Boolean.FALSE, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/license/token", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompanyLicensePublic> checkAccessToken(@RequestBody CompanyLicensePublic licensePublic,
			@RequestHeader("Accept-Language") String locale) throws Exception {
		if (!Strings.isNullOrEmpty(licensePublic.getAccessToken())) {
			String accessToken = licensePublic.getAccessToken();
			CompanyLicensePrivate licensePrivate = licenseRepository.findByProbeId(licensePublic.getProbeId());

			if (licensePrivate != null) {
				boolean isTokenValid = false;
				isTokenValid = tokenAuthenticationService.validateToken(accessToken, licensePrivate.getTokenSecret());

				if (isTokenValid) {
					// Generate new access token if validity is smaller than the value defined in
					// settings
					List<Settings> settings = settingsRepository.findAll();
					if (settings != null && settings.size() == 1) {
						long tokenMinValidityTime = portalUtils.convertTimeUnitsToMilis(settings.get(0).getAccessTokenProbeRestValidityTime(),
								settings.get(0).getAccessTokenProbeRestValidityTimeUnit());
						long tokenExpirationTime = tokenAuthenticationService.getTokenExpirationDate(accessToken, licensePrivate.getTokenSecret())
								.getTime();

						if (tokenExpirationTime - System.currentTimeMillis() <= tokenMinValidityTime) {
							if (!LicenseDateUtil.isLicenseExpired(licensePrivate.getExpirationDate())) {
								CompanyGroup group = groupRepository.find(licensePrivate.getGroupId());
								if (group != null) {
									accessToken = tokenAuthenticationService.addLicenseAuthentication(licensePrivate.getProbeId(), group, licensePrivate);
									licensePrivate.setAccessToken(accessToken);
									licenseRepository.update(licensePrivate);
								}
							}
						}
					}
					log.debug("Probe access token is still valid.");
					return new ResponseEntity<>(licensePrivate, HttpStatus.OK);
				} else {
					if (!LicenseDateUtil.isLicenseExpired(licensePrivate.getExpirationDate())) {
						CompanyGroup group = groupRepository.find(licensePrivate.getGroupId());
						if (group != null) {
							accessToken = tokenAuthenticationService.addLicenseAuthentication(licensePrivate.getProbeId(), group, licensePrivate);
							licensePrivate.setAccessToken(accessToken);
							licenseRepository.update(licensePrivate);
							/*
							 * IMPORANT: Always use getPublicLicense if sending data to the probe because only than the private sensitive data is cleared.
							 * Using a normal cast wont.
							 */
							return new ResponseEntity<>(licensePrivate.getPublicLicense(), HttpStatus.OK);
						}
					}
				}
			}
		}
		return new ResponseEntity<>(licensePublic, HttpStatus.OK);
	}
}
