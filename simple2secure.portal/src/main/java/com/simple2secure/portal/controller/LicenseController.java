/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicense;
import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.User;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.GroupRepository;
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

import ro.fortsoft.licensius.LicenseGenerator;
import ro.fortsoft.licensius.LicenseManager;
import ro.fortsoft.licensius.OrderedProperties;

@RestController
public class LicenseController {

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
	DataInitialization dataInitialization;

	@Autowired
	TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	PortalUtils portalUtils;

	private static Logger log = LoggerFactory.getLogger(LicenseController.class);

	RestTemplate restTemplate = new RestTemplate();

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
	public ResponseEntity<String> activateLicense(@RequestBody CompanyLicenseObj licenseObj, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {
		if (licenseObj != null) {

			String groupId = licenseObj.getGroupId();
			String licenseId = licenseObj.getLicenseId();
			String probeId = licenseObj.getProbeId();

			if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(licenseId) && !Strings.isNullOrEmpty(probeId)) {
				CompanyGroup group = groupRepository.find(groupId);
				CompanyLicense license = licenseRepository.find(licenseId);

				if (group == null || license == null) {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
							HttpStatus.NOT_FOUND);
				} else {
					if (!Strings.isNullOrEmpty(license.getUserId())) {
						license.setTokenSecret(portalUtils.alphaNumericString(20));
						String accessToken = tokenAuthenticationService.addLicenseAuthentication(probeId, group, license);

						if (!Strings.isNullOrEmpty(accessToken)) {
							/*
							 * TODO: Check why the service data initialization is used.
							 */
							if (!dataInitialization.addConfiguration(probeId, group.getId())) {
								return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
										HttpStatus.NOT_FOUND);
							}

							if (!dataInitialization.addProcessors(probeId, group.getId())) {
								return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
										HttpStatus.NOT_FOUND);
							}

							if (!dataInitialization.addQueries(probeId, group.getId())) {
								return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
										HttpStatus.NOT_FOUND);
							}

							if (!dataInitialization.addSteps(probeId, group.getId())) {
								return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
										HttpStatus.NOT_FOUND);
							}

							license.setProbeId(probeId);
							license.setAccessToken(accessToken);
							license.setActivated(true);

							licenseRepository.update(license);

							return new ResponseEntity(accessToken, HttpStatus.OK);
						} else {
							return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
									HttpStatus.NOT_FOUND);
						}
					} else {
						return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
								HttpStatus.NOT_FOUND);
					}
				}
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
						HttpStatus.NOT_FOUND);
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/api/license/{groupId}/{userId}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getLicense(@PathVariable("groupId") String groupId, @PathVariable("userId") String userId,
			@RequestHeader("Accept-Language") String locale) throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		httpHeaders.setContentDispositionFormData("attachment", "license.zip");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
		ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

		CompanyGroup group = groupRepository.find(groupId);

		if (group.getCurrentNumberOfLicenseDownloads() < group.getMaxNumberOfLicenseDownloads()) {
			createLicense(groupId, userId, group);

			ArrayList<File> files = new ArrayList<>();

			File publicKey = new File(LicenseManager.PUBLIC_KEY_FILE);
			File certificate = new File(LicenseManager.LICENSE_FILE);

			files.add(publicKey);
			files.add(certificate);

			for (File file : files) {
				zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
				FileInputStream fileInputStream = new FileInputStream(file);
				IOUtils.copy(fileInputStream, zipOutputStream);

				fileInputStream.close();
				zipOutputStream.closeEntry();
			}

			if (zipOutputStream != null) {
				zipOutputStream.finish();
				zipOutputStream.flush();
				IOUtils.closeQuietly(zipOutputStream);
			}

			IOUtils.closeQuietly(bufferedOutputStream);
			IOUtils.closeQuietly(byteArrayOutputStream);

			group.setCurrentNumberOfLicenseDownloads(group.getCurrentNumberOfLicenseDownloads() + 1);
			groupRepository.update(group);

			return new ResponseEntity(byteArrayOutputStream.toByteArray(), HttpStatus.OK);
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("max_license_number_exceeded", locale)),
					HttpStatus.NOT_FOUND);
		}

	}

	@RequestMapping(value = "/api/license/{licenseId}/{groupId}/{probeId}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> checkLicense(@PathVariable("licenseId") String licenseId, @PathVariable("groupId") String groupId,
			@PathVariable("probeId") String probeId, @RequestHeader("Accept-Language") String locale) throws Exception {
		CompanyLicense license = licenseRepository.find(licenseId);
		if (license != null && license.isActivated()) {
			if (license.getGroupId().equalsIgnoreCase(groupId) && license.getProbeId().equalsIgnoreCase(probeId)) {
				return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
			}
		}
		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
	}

	/**
	 * TODO: maybe it will be needed to change this function to boolean to check if this license can be created.
	 *
	 * @param groupId
	 * @throws Exception
	 */
	public void createLicense(String groupId, String userId, CompanyGroup group) throws Exception {
		Properties properties = new OrderedProperties();
		CompanyLicense companyLicense = new CompanyLicense(groupId, userId, false);
		companyLicense.setExpirationDate(group.getLicenseExpirationDate());
		ObjectId licenseId = licenseRepository.saveAndReturnId(companyLicense);
		properties.setProperty("expirationDate", group.getLicenseExpirationDate());
		properties.setProperty("groupId", groupId);
		properties.setProperty("licenseId", licenseId.toString());
		LicenseGenerator.generateLicense(properties, "private.key");
	}

	/**
	 * This function extracts the userId from the groupId by checking first if the group with the provided groupId exists in the database, and
	 * then if it is true it extracts the addedByUserId parameter from the group object which is the correct userId
	 *
	 * @param groupId
	 * @return
	 */
	public String getUserIdFromGroup(String groupId) {
		CompanyGroup group = groupRepository.find(groupId);
		if (group == null) {
			return null;
		} else {
			if (Strings.isNullOrEmpty(group.getAddedByUserId())) {
				return null;
			} else {
				User user = userRepository.find(group.getAddedByUserId());
				if (user == null) {
					return null;
				} else {
					return group.getAddedByUserId();
				}
			}
		}
	}

	@RequestMapping(value = "/api/license/token", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompanyLicenseObj> checkAccessToken(@RequestBody CompanyLicenseObj licenseObj,
			@RequestHeader("Accept-Language") String locale) throws Exception {
		if (!Strings.isNullOrEmpty(licenseObj.getAuthToken())) {
			String accessToken = licenseObj.getAuthToken();
			CompanyLicense license = licenseRepository.findByProbeId(licenseObj.getProbeId());

			if (license != null) {
				boolean isTokenValid = false;
				isTokenValid = tokenAuthenticationService.validateToken(accessToken, license.getTokenSecret());

				if (isTokenValid) {
					// Generate new access token if validity is smaller than the value defined in
					// settings
					List<Settings> settings = settingsRepository.findAll();
					if (settings != null) {
						if (settings.size() == 1) {
							long tokenMinValidityTime = portalUtils.convertTimeUnitsToMilis(settings.get(0).getAccessTokenProbeRestValidityTime(),
									settings.get(0).getAccessTokenProbeRestValidityTimeUnit());
							long tokenExpirationTime = tokenAuthenticationService.getTokenExpirationDate(accessToken, license.getTokenSecret()).getTime();

							if (tokenExpirationTime - System.currentTimeMillis() <= tokenMinValidityTime) {
								if (!portalUtils.isLicenseExpired(license.getExpirationDate())) {
									CompanyGroup group = groupRepository.find(license.getGroupId());
									if (group != null) {
										accessToken = tokenAuthenticationService.addLicenseAuthentication(license.getProbeId(), group, license);
										license.setAccessToken(accessToken);
										licenseObj.setAuthToken(accessToken);
										licenseRepository.update(license);
									}
								}
							}
						}
					}
					log.debug("Probe access token is still valid.");
					return new ResponseEntity<CompanyLicenseObj>(licenseObj, HttpStatus.OK);
				} else {
					if (!portalUtils.isLicenseExpired(license.getExpirationDate())) {
						CompanyGroup group = groupRepository.find(license.getGroupId());
						if (group != null) {
							accessToken = tokenAuthenticationService.addLicenseAuthentication(license.getProbeId(), group, license);
							license.setAccessToken(accessToken);
							licenseObj.setAuthToken(accessToken);
							licenseRepository.update(license);
							return new ResponseEntity<CompanyLicenseObj>(licenseObj, HttpStatus.OK);
						}
					}
				}
			}
		}
		return new ResponseEntity<CompanyLicenseObj>(licenseObj, HttpStatus.OK);
	}
}
