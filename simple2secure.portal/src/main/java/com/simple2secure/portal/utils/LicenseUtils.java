package com.simple2secure.portal.utils;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.Settings;
import com.simple2secure.commons.license.LicenseDateUtil;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.commons.time.TimeUtils;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.security.auth.TokenAuthenticationService;

@Component
public class LicenseUtils {
	private static Logger log = LoggerFactory.getLogger(LicenseUtil.class);

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	SettingsRepository settingsRepository;

	@Autowired
	LicensePlanRepository licensePlanRepository;

	@Autowired
	TokenAuthenticationService tokenAuthenticationService;

	/**
	 * Activates the provided license and creates a private license {@link CompanyLicensePrivate} and stores it in the database. If the
	 * license has not been activated before, it creates a secret for this license otherwise just a new access token is created. Thus, it is
	 * possible to perform this operation multiple times in order to provide a long term access token renewal process. Depending if the call
	 * is coming from a PROBE or a POD different tables and authentications are created.
	 *
	 * @param licensePublic
	 *          The public license object which must be provided in order to activate it and generate an access token.
	 * @param podActivation
	 *          True if the activation is coming from a POD, otherwise false.
	 * @return
	 */
	public CompanyLicensePublic activateLicense(CompanyLicensePublic licensePublic, boolean podActivation) {
		if (licensePublic != null) {
			String groupId = licensePublic.getGroupId();
			String licenseId = licensePublic.getLicenseId();
			String deviceId = licensePublic.getDeviceId();
			String hostname = licensePublic.getHostname();

			if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(licenseId) && !Strings.isNullOrEmpty(deviceId)) {
				CompanyGroup group = groupRepository.find(groupId);
				CompanyLicensePrivate license = null;
				/*
				 * Check if a license has already been activated and associated with this POD or PROBE
				 */
				license = licenseRepository.findByLicenseIdAndDeviceId(licenseId, deviceId, podActivation);

				/*
				 * If no license has been activated and associated with the POD or PROBE we need to create one for them.
				 */
				if (license == null) {
					List<CompanyLicensePrivate> licenses = licenseRepository.findByLicenseId(licenseId);
					if (licenses != null && licenses.size() > 0) {
						CompanyLicensePrivate tempLicense = licenses.get(0);
						if (!Strings.isNullOrEmpty(tempLicense.getDeviceId())) {
							license = tempLicense.copyLicense();
						} else {
							license = tempLicense;
						}
					}
				}

				/*
				 * Create a new access token for the POD or PROBE and update the license
				 */
				if (group != null && license != null) {
					if (!license.isActivated()) {
						license.setTokenSecret(RandomStringUtils.randomAlphanumeric(20));
					}

					String accessToken = null;

					if (podActivation) {
						accessToken = tokenAuthenticationService.addPodAuthentication(deviceId, group, license);
					} else {
						accessToken = tokenAuthenticationService.addProbeAuthentication(deviceId, group, license);
					}

					if (!Strings.isNullOrEmpty(accessToken)) {
						if (Strings.isNullOrEmpty(license.getDeviceId())) {
							license.setDeviceId(deviceId);
						}
						license.setAccessToken(accessToken);
						license.setHostname(hostname);
						license.setLastOnlineTimestamp(System.currentTimeMillis());

						if (!license.isActivated()) {
							license.setActivated(true);
						}

						licenseRepository.save(license);

						return license;
					}
				}
			}
		}
		return licensePublic;
	}

	public CompanyLicensePublic checkToken(CompanyLicensePublic licensePublic, boolean checkForPod) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(licensePublic.getAccessToken())) {
			String accessToken = licensePublic.getAccessToken();
			CompanyLicensePrivate licensePrivate = null;

			String deviceId = licensePublic.getDeviceId();

			if (checkForPod && !Strings.isNullOrEmpty(deviceId)) {
				licensePrivate = licenseRepository.findByDeviceId(deviceId);
			} else {
				return null;
			}

			if (!Strings.isNullOrEmpty(accessToken)) {
				boolean isTokenValid = tokenAuthenticationService.validateToken(accessToken, licensePrivate.getTokenSecret());

				if (isTokenValid) {
					/*
					 * Generate new access token if validity is smaller than the value defined in settings
					 */
					List<Settings> settings = settingsRepository.findAll();
					if (settings != null && settings.size() == 1) {
						long tokenMinValidityTime = TimeUtils.convertTimeUnitsToMilis(settings.get(0).getAccessTokenProbeRestValidityTime(),
								settings.get(0).getAccessTokenProbeRestValidityTimeUnit());
						long tokenExpirationTime = tokenAuthenticationService.getTokenExpirationDate(accessToken, licensePrivate.getTokenSecret())
								.getTime();

						if (tokenExpirationTime - System.currentTimeMillis() <= tokenMinValidityTime) {
							if (!LicenseDateUtil.isLicenseExpired(licensePrivate.getExpirationDate())) {
								CompanyGroup group = groupRepository.find(licensePrivate.getGroupId());
								if (checkForPod) {
									accessToken = tokenAuthenticationService.addPodAuthentication(licensePrivate.getDeviceId(), group, licensePrivate);
								} else {
									accessToken = tokenAuthenticationService.addProbeAuthentication(licensePrivate.getDeviceId(), group, licensePrivate);
								}
								licensePrivate.setAccessToken(accessToken);
								licenseRepository.update(licensePrivate);
							}
						}
					}
				}
				log.debug("Access token is still valid.");
				return licensePrivate.getPublicLicense();
			} else {
				log.debug("Access token is not valid anymore, trying to create a new one");

				if (!LicenseDateUtil.isLicenseExpired(licensePrivate.getExpirationDate())) {
					log.debug("License is still valid, creating a new access token");

					CompanyGroup group = groupRepository.find(licensePrivate.getGroupId());

					if (group != null) {

						if (checkForPod) {
							accessToken = tokenAuthenticationService.addPodAuthentication(licensePrivate.getDeviceId(), group, licensePrivate);
						} else {
							accessToken = tokenAuthenticationService.addProbeAuthentication(licensePrivate.getDeviceId(), group, licensePrivate);
						}

						licensePrivate.setAccessToken(accessToken);
						licenseRepository.save(licensePrivate);
						/*
						 * IMPORANT: Always use getPublicLicense if sending data to the PROBE or POD because only than the private sensitive data is
						 * cleared. Using a normal cast wont.
						 */
						return licensePrivate.getPublicLicense();
					}
				}
			}
		}
		return null;
	}
}
