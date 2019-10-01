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
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.Settings;
import com.simple2secure.commons.license.LicenseDateUtil;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.commons.time.TimeUtils;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TokenRepository;
import com.simple2secure.portal.security.auth.TokenAuthenticationService;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.DataInitialization;
import com.simple2secure.portal.utils.PortalUtils;

@RestController
@RequestMapping("/api/license")
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
	PortalUtils portalUtils;

	@Autowired
	RestTemplate restTemplate;

	// private Gson gson = new Gson();

	@PostConstruct
	public void initialize() {
		licenseFilePath = LicenseUtil.getLicensePath(licenseFilePath);
		publicKeyPath = LicenseUtil.getLicenseKeyPath(publicKeyPath, licenseFilePath);
		privateKeyPath = LicenseUtil.getLicenseKeyPath(privateKeyPath, licenseFilePath);
		LicenseUtil.initialize(licenseFilePath, privateKeyPath, publicKeyPath);
	}

	/**
	 *
	 * @param groupId
	 * @param licenseId
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/activatePod",
			method = RequestMethod.POST,
			consumes = "application/json")
	public ResponseEntity<String> activatePod(@RequestBody CompanyLicensePublic licensePod, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {
		if (licensePod != null) {
			String groupId = licensePod.getGroupId();
			String licenseId = licensePod.getLicenseId();
			String podId = licensePod.getPodId();
			String hostname = licensePod.getHostname();
			// String configuration = licensePod.getConfiguration();

			if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(licenseId) && !Strings.isNullOrEmpty(podId)
					&& !Strings.isNullOrEmpty(hostname)) {
				CompanyGroup group = groupRepository.find(groupId);
				CompanyLicensePrivate license = licenseRepository.findByLicenseIdAndPodId(licenseId, podId);

				if (license == null) {
					List<CompanyLicensePrivate> licenses = licenseRepository.findByLicenseId(licenseId);
					if (licenses != null && licenses.size() > 0) {
						CompanyLicensePrivate tempLicense = licenses.get(0);
						if (!Strings.isNullOrEmpty(tempLicense.getPodId())) {
							license = tempLicense.copyLicense();
						} else {
							license = tempLicense;
						}
					}
				}

				if (group != null && license != null) {

					license.setTokenSecret(RandomStringUtils.randomAlphanumeric(20));
					String accessToken = tokenAuthenticationService.addPodAuthentication(podId, group, license);
					if (!Strings.isNullOrEmpty(accessToken)) {

						if (Strings.isNullOrEmpty(license.getPodId())) {
							license.setPodId(podId);
						}

						license.setAccessToken(accessToken);
						license.setActivated(true);
						license.setHostname(licensePod.getHostname());
						license.setLastOnlineTimestamp(System.currentTimeMillis());

						licenseRepository.save(license);

						// save pod configuration
						// if (!Strings.isNullOrEmpty(configuration) && !configuration.equalsIgnoreCase("NONE")) {
						//
						// Test[] podTestList = gson.fromJson(configuration, Test[].class);
						//
						// if (podTestList != null && podTestList.length > 0) {
						// for (Test podTest : podTestList) {
						// if (podTest != null) {
						// if (Strings.isNullOrEmpty(podTest.getPodId())) {
						// podTest.setPodId(podId);
						// podTest.setHostname(licensePod.getHostname());
						// }
						// testRepository.save(podTest);
						// }
						// }
						// }
						// }
					}
					return new ResponseEntity(accessToken, HttpStatus.OK);
				}
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
				HttpStatus.NOT_FOUND);

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
	@RequestMapping(
			value = "/activateProbe",
			method = RequestMethod.POST,
			consumes = "application/json")
	public ResponseEntity<String> activateLicense(@RequestBody CompanyLicensePublic licensePublic,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		if (licensePublic != null) {

			String groupId = licensePublic.getGroupId();
			String licenseId = licensePublic.getLicenseId();
			String probeId = licensePublic.getProbeId();

			if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(licenseId) && !Strings.isNullOrEmpty(probeId)) {
				CompanyGroup group = groupRepository.find(groupId);
				CompanyLicensePrivate license = licenseRepository.findByLicenseIdAndProbeId(licenseId, probeId);
				if (license == null) {
					List<CompanyLicensePrivate> licenses = licenseRepository.findByLicenseId(licenseId);
					if (licenses != null && licenses.size() > 0) {
						CompanyLicensePrivate tempLicense = licenses.get(0);
						if (!Strings.isNullOrEmpty(tempLicense.getProbeId())) {
							license = tempLicense.copyLicense();
						} else {
							license = tempLicense;
						}
					}
				}

				if (group != null && license != null) {

					license.setTokenSecret(RandomStringUtils.randomAlphanumeric(20));
					String accessToken = tokenAuthenticationService.addProbeAuthentication(probeId, group, license);

					if (!Strings.isNullOrEmpty(accessToken)) {

						license.setProbeId(probeId);
						license.setAccessToken(accessToken);
						license.setActivated(true);

						licenseRepository.save(license);

						return new ResponseEntity(accessToken, HttpStatus.OK);
					}
				}
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_during_activation", locale)),
				HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(
			value = "/{groupId}/{userId}",
			method = RequestMethod.GET)
	public ResponseEntity<byte[]> getLicense(@PathVariable("groupId") String groupId, @PathVariable("userId") String userId,
			@RequestHeader("Accept-Language") String locale) throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		httpHeaders.setContentDispositionFormData("attachment", "license.zip");

		CompanyGroup group = groupRepository.find(groupId);
		Context context = contextRepository.find(group.getContextId());

		if (context != null) {
			LicensePlan licensePlan = licensePlanRepository.find(context.getLicensePlanId());
			if (licensePlan != null) {
				if (context.getCurrentNumberOfLicenseDownloads() < licensePlan.getMaxNumberOfDownloads()) {

					String expirationDate = LicenseDateUtil.getLicenseExpirationDate(licensePlan.getValidity(), licensePlan.getValidityUnit());
					/*
					 * TODO: Generates a new license for each request. Should not be the case
					 */
					List<CompanyLicensePrivate> companyLicenses = licenseRepository.findByGroupId(groupId);
					String licenseId = LicenseUtil.generateLicenseId();
					CompanyLicensePrivate companyLicense = new CompanyLicensePrivate(groupId, licenseId, expirationDate, false);

					if (companyLicenses != null && companyLicenses.size() > 0) {
						licenseId = companyLicenses.get(companyLicenses.size() - 1).getLicenseId();
						companyLicense = new CompanyLicensePrivate(groupId, licenseId, expirationDate, false);
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
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("max_license_number_exceeded", locale)),
				HttpStatus.NOT_FOUND);
	}

	@RequestMapping(
			value = "/{licenseId}/{groupId}/{probeId}",
			method = RequestMethod.GET)
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

	@RequestMapping(
			value = "/token",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
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
						long tokenMinValidityTime = TimeUtils.convertTimeUnitsToMilis(settings.get(0).getAccessTokenProbeRestValidityTime(),
								settings.get(0).getAccessTokenProbeRestValidityTimeUnit());
						long tokenExpirationTime = tokenAuthenticationService.getTokenExpirationDate(accessToken, licensePrivate.getTokenSecret())
								.getTime();

						if (tokenExpirationTime - System.currentTimeMillis() <= tokenMinValidityTime) {
							if (!LicenseDateUtil.isLicenseExpired(licensePrivate.getExpirationDate())) {
								CompanyGroup group = groupRepository.find(licensePrivate.getGroupId());
								if (group != null) {
									accessToken = tokenAuthenticationService.addProbeAuthentication(licensePrivate.getProbeId(), group, licensePrivate);
									licensePrivate.setAccessToken(accessToken);
									licenseRepository.update(licensePrivate);
								}
							}
						}
					}
					log.debug("Probe access token is still valid.");
					return new ResponseEntity<>(licensePrivate.getPublicLicense(), HttpStatus.OK);
				} else {
					if (!LicenseDateUtil.isLicenseExpired(licensePrivate.getExpirationDate())) {
						CompanyGroup group = groupRepository.find(licensePrivate.getGroupId());
						if (group != null) {
							accessToken = tokenAuthenticationService.addProbeAuthentication(licensePrivate.getProbeId(), group, licensePrivate);
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
