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
package com.simple2secure.probe.license;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.DeviceStatus;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.file.ZIPUtils;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.commons.license.License;
import com.simple2secure.commons.license.LicenseDateUtil;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.commons.rest.RESTUtils;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.utils.DBUtil;

public class LicenseController {

	private static Logger log = LoggerFactory.getLogger(LicenseController.class);

	public LicenseController() {
	}

	/**
	 * Obtains the license from the specified path. It requires a license ZIP file as input, containing the license.dat and the public key for
	 * verification. If not an exception
	 *
	 * @param importFilePath
	 * @return
	 * @throws Exception
	 */
	public CompanyLicensePublic loadLicenseFromPath(String importFilePath) throws Exception {
		CompanyLicensePublic license = null;
		File inputFile = new File(importFilePath);

		if (inputFile != null && inputFile.exists()) {
			List<File> unzippedFiles = ZIPUtils.unzipImportedFile(inputFile);
			if (LicenseUtil.checkLicenseDirValidity(unzippedFiles)) {
				License downloadedLicense = LicenseUtil.getLicense(unzippedFiles);
				if (checkLicenseProps(downloadedLicense)) {
					return createLicenseForAuth(downloadedLicense);
				} else {
					log.error("The required license properties couldn't be obtained from the ZIP file {}", importFilePath);
				}
			} else {
				log.error("Unzipping file {} didn't result in correct amount of files!", importFilePath);
			}
		} else {
			log.error("Specified ZIP file {} doesn't exist!", importFilePath);
		}
		return license;
	}

	/**
	 * Checks if there is a license stored in the DB. Checks if the license found in DB is expired. Checks if the license is activated.
	 * Depending on the outcome either {@link StartConditions#LICENSE_NOT_AVAILABLE} if there is no license stored in the DB is returned.
	 * {@link StartConditions#LICENSE_EXPIRED} if the license is in DB but expired is returned. {@link StartConditions#LICENSE_NOT_ACTIVATED}
	 * is returned if the license is not expired but it is not activated set. {@link StartConditions#LICENSE_VALID} if the license is not
	 * expired and it is activated.
	 *
	 * @return The {@link StartConditions} which corresponds to the current state.
	 */
	public StartConditions checkProbeStartConditions() {
		CompanyLicensePublic license = loadLicenseFromDB();
		if (license != null) {
			if (!LicenseDateUtil.isLicenseExpired(license.getExpirationDate())) {
				if (license.isActivated()) {
					ProbeConfiguration.isLicenseValid = true;
					ProbeConfiguration.probeId = license.getDeviceId();
					ProbeConfiguration.groupId = license.getGroupId();
					ProbeConfiguration.authKey = license.getAccessToken();
					return StartConditions.LICENSE_VALID;
				}
				return StartConditions.LICENSE_NOT_ACTIVATED;
			}
			return StartConditions.LICENSE_EXPIRED;
		}
		return StartConditions.LICENSE_NOT_AVAILABLE;
	}

	/**
	 * Checks if the properties in the license object are set (not null or empty).
	 *
	 * @param license
	 *          The {@link License} for which the properties are checked.
	 * @return True if the properties in the license object are set, false if only one property is null or empty
	 */
	public boolean checkLicenseProps(License license) {
		Boolean isLicensePropsValid = false;

		if (license == null) {
			return isLicensePropsValid;
		}

		if (!Strings.isNullOrEmpty(license.getProperty("groupId")) && !Strings.isNullOrEmpty(license.getProperty("licenseId"))
				&& !Strings.isNullOrEmpty(license.getExpirationDateAsString())) {
			isLicensePropsValid = true;
		}
		return isLicensePropsValid;
	}

	/**
	 * Updates the license in the DB with the local license.
	 *
	 * @param license
	 *          The license which should be updated in the database.
	 */
	public void updateLicenseInDB(CompanyLicensePublic license) {
		if (license != null) {
			CompanyLicensePublic loadedLicense = loadLicenseFromDB();
			if (loadedLicense != null) {
				license.setId(loadedLicense.getId());
			}

			DBUtil.getInstance().merge(license);
		}
	}

	/**
	 * Loads the license from the data base.
	 *
	 * @return The {@link CompanyLicensePublic} from stored in the database.
	 */
	public CompanyLicensePublic loadLicenseFromDB() {
		List<CompanyLicensePublic> licenses = DBUtil.getInstance().findAll(CompanyLicensePublic.class);

		if (licenses.size() != 1) {
			return null;
		} else {
			return licenses.get(0);
		}
	}

	/**
	 * Tries to activate the provided {@link CompanyLicensePublic} via the Portal API. If successful the current access token is obtained and
	 * stored in the {@link ProbeConfiguration}.
	 *
	 * @param license
	 *          The license which should be used for activation.
	 * @return
	 */
	public boolean activateLicense(CompanyLicensePublic license) {
		if (license != null) {
			String authToken = RESTUtils.sendPost(LoadedConfigItems.getInstance().getLicenseAPI() + "/authenticate", license);
			if (authToken != null) {
				activateLicenseInDB(authToken, license);

				ProbeConfiguration.authKey = authToken;
				ProbeConfiguration.probeId = license.getDeviceId();
				ProbeConfiguration.groupId = license.getGroupId();
				ProbeConfiguration.setAPIAvailablitity(true);
				log.info("License successfully activated and AuthToken obtained");
				return true;
			} else {
				log.error("A problem occured while loading the license from path.");
			}
		}
		return false;
	}

	/**
	 * Sets the needed Flags in the DB to mark the license activated.
	 *
	 * @param authToken
	 *          The token which the server returns if the activation succeeded.
	 * @param license
	 *          The {@link CompanyLicensePublic} which should be set to activated.
	 *
	 */
	public void activateLicenseInDB(String authToken, CompanyLicensePublic license) {
		if (!Strings.isNullOrEmpty(authToken)) {
			CompanyLicensePublic receivedLicense = JSONUtils.fromString(authToken, CompanyLicensePublic.class);
			if (receivedLicense != null) {
				if (!receivedLicense.getDeviceId().equals(license.getDeviceId())) {
					log.error("Received license doesn't contain the same device ID, needs to be verified, continuing for now!");
				}
				license.setAccessToken(receivedLicense.getAccessToken());
				license.setActivated(true);
				updateLicenseInDB(license);
			} else {
				if (authToken.contains("accessToken")) {
					int start = authToken.indexOf("accessToken") + "accessToken".length();
					int actualStart = authToken.indexOf(":\"", start);
					int actualEnd = authToken.indexOf("\"", actualStart);
					String accessToken = authToken.substring(actualStart, actualEnd);
					if (!Strings.isNullOrEmpty(accessToken)) {
						license.setAccessToken(accessToken.trim());
						license.setActivated(true);
						updateLicenseInDB(license);
					}
				}
			}
		}
	}

	/**
	 * Creates a CompanyLicenseObj to send it to the server for authentication. Also checks if there is already a license stored in the DB, if
	 * there is a license stored it just updates the license. If there is no license stored in the DB, it creates a new entry in the DB.
	 *
	 * @param license
	 *          The {@link License} object which should be used for creating a {@link CompanyLicensePublic} for authentication
	 * @return The {@link CompanyLicensePublic} for authentication.
	 *
	 */
	public CompanyLicensePublic createLicenseForAuth(License license) {
		String probeId = "";
		String groupId, licenseId, expirationDate, hostname = "LOCALHOST";

		if (license == null) {
			return null;
		}
		/*
		 * Obtain the important parameters from the license object.
		 */
		groupId = license.getProperty("groupId");
		licenseId = license.getProperty("licenseId");
		expirationDate = license.getExpirationDateAsString();
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.error("Couldn't obtain hostname locally.");
		}

		/*
		 * Obtain license stored in DB if any.
		 */
		CompanyLicensePublic storedLicense = loadLicenseFromDB();

		/*
		 * If license is already available in DB, update the content with the one provided as input. Otherwise create a new Probe ID and create
		 * a new CompanyLisnecePublic object from it.
		 */
		if (storedLicense != null) {
			if (Strings.isNullOrEmpty(storedLicense.getDeviceId())) {
				probeId = UUID.randomUUID().toString();
				storedLicense.setDeviceId(probeId);
			}
			storedLicense.setGroupId(groupId);
			storedLicense.setLicenseId(licenseId);
			storedLicense.setExpirationDate(expirationDate);
		} else {
			probeId = UUID.randomUUID().toString();
			storedLicense = new CompanyLicensePublic(groupId, licenseId, expirationDate, probeId);
		}
		storedLicense.setStatus(DeviceStatus.ONLINE);
		storedLicense.setHostname(hostname);
		/*
		 * Update the license in the local DB.
		 */
		updateLicenseInDB(storedLicense);
		return storedLicense;
	}

	/**
	 * Verifies the validity of the stored license using the authenticate service from the Portal. If the response contains a
	 * {@link CompanyLicensePublic} it returns it otherwise null is returned.
	 *
	 * @return
	 */
	public CompanyLicensePublic checkTokenValidity() {
		CompanyLicensePublic license = loadLicenseFromDB();
		if (license != null) {
			String response = RESTUtils.sendPost(LoadedConfigItems.getInstance().getLicenseAPI() + "/authenticate", license);
			if (!Strings.isNullOrEmpty(response)) {
				return JSONUtils.fromString(response, CompanyLicensePublic.class);
			}
			log.error("The reponse from the Token service from the Portal didn't return a CompanyLicensePublic object.");
		} else {
			log.error("Couldn't find license in DB. Need to do something here");
		}
		return null;

	}
}
