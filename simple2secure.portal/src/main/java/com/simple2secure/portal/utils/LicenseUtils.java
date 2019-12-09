package com.simple2secure.portal.utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.portal.model.LicenseActivation;
import com.simple2secure.portal.repository.DeviceInfoRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.security.auth.TokenAuthenticationService;
import com.simple2secure.portal.service.MessageByLocaleService;

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
	DeviceInfoRepository deviceInfoRepository;

	@Autowired
	TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	MessageByLocaleService messageByLocaleService;

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
	 * @throws UnsupportedEncodingException
	 */
	public LicenseActivation authenticateLicense(CompanyLicensePublic licensePublic, boolean podActivation, String locale)
			throws UnsupportedEncodingException {
		LicenseActivation activation = new LicenseActivation(false);

		activation.setMessage(messageByLocaleService.getMessage("problem_during_activation", locale));

		if (licensePublic != null) {
			String groupId = licensePublic.getGroupId();
			String licenseId = licensePublic.getLicenseId();
			String deviceId = licensePublic.getDeviceId();

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
						CompanyLicensePrivate tempLicense = licenses.get(licenses.size() - 1);
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

					accessToken = tokenAuthenticationService.addDeviceAuthentication(deviceId, group, license);

					if (!Strings.isNullOrEmpty(accessToken)) {
						if (Strings.isNullOrEmpty(license.getDeviceId())) {
							license.setDeviceId(deviceId);
						}
						license.setAccessToken(accessToken);
						license.setDeviceIsPod(podActivation);
						if (!license.isActivated()) {
							license.setActivated(true);
						}

						licenseRepository.save(license);
						activation.setAccessToken(accessToken);
						activation.setSuccess(true);
						return activation;
					}
				} else {
					activation.setMessage(messageByLocaleService.getMessage("specified_license_not_available", locale));
				}
			} else {
				activation.setMessage(messageByLocaleService.getMessage("specified_license_not_available", locale));
			}
		}
		return activation;
	}

	public String getPayloadFromTheToken(String token) {
		String[] split_string = token.split("\\.");
		String base64EncodedBody = split_string[1];

		Base64 base64Url = new Base64(true);

		String payload = new String(base64Url.decode(base64EncodedBody));

		return payload;

	}

	public String getFieldFromPayload(String payload, String field) {
		JsonNode node = JSONUtils.fromString(payload);
		return node.findValue(field).asText();
	}
}
