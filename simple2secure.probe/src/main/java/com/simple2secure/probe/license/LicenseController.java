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
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.DeviceStatus;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.file.FileUtil;
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
	 * Checks if there is a license stored in the DB. Checks if the license is activated. If not, it is activated and if the activation was
	 * successful {@link StartConditions#LICENSE_VALID} is returned. If the license is expired or the activation was not successful it is
	 * tried to obtain a license from the specified license path. If a valid license is available it is activated and
	 * {@link StartConditions#LICENSE_VALID} is returned. Otherwise {@link StartConditions.LICENSE_NOT_AVAILABLE} is returned.
	 *
	 * @return The {@link StartConditions} which corresponds to the current state.
	 */
	public StartConditions checkLicenseValidity() {
		CompanyLicensePublic license = loadLicenseFromDB();
		if (license != null) {
			ProbeConfiguration.probeId = license.getDeviceId();
			if (!LicenseDateUtil.isLicenseExpired(license.getExpirationDate())) {
				if (license.isActivated()) {
					ProbeConfiguration.isLicenseValid = true;
					ProbeConfiguration.groupId = license.getGroupId();
					ProbeConfiguration.authKey = license.getAccessToken();
					return StartConditions.LICENSE_VALID;
				} else {
					if (authenticateLicense()) {
						return StartConditions.LICENSE_VALID;
					}
				}
			}
		}
		license = loadLicenseFromPath();
		if (license != null && authenticateLicense()) {
			return StartConditions.LICENSE_VALID;
		}
		return StartConditions.LICENSE_NOT_AVAILABLE;
	}

	/**
	 * Obtains the license from the specified path. It requires a license ZIP file as input, containing the license.dat and the public key for
	 * verification. If not an exception is thrown. If the importFilePath specifies a folder all ZIP files are obtained and the newest is used
	 * as input. If a file is specified this is used.
	 *
	 * @param importFilePath
	 *          The path to the license folder or file.
	 * @return The {@link CompanyLicensePublic} object obtained from the file if any.
	 * @throws Exception
	 */
	public CompanyLicensePublic loadLicenseFromPath() {
		CompanyLicensePublic license = null;
		try {
			String licensePath = ProbeConfiguration.licensePath;
			if (FileUtil.fileOrFolderExists(licensePath)) {
				File inputFile = null;
				if (FileUtil.isDirectory(licensePath)) {
					List<File> files = FileUtil.getFilesFromDirectory(licensePath, false, Arrays.asList(new String[] { "zip" }));
					inputFile = null;
					for (File file : files) {
						if (inputFile == null) {
							inputFile = file;
						}
						if (inputFile.lastModified() < file.lastModified()) {
							inputFile = file;
						}
					}
				} else {
					inputFile = new File(licensePath);
				}

				if (inputFile != null && inputFile.exists()) {
					List<File> unzippedFiles = ZIPUtils.unzipImportedFile(inputFile);
					if (LicenseUtil.checkLicenseDirValidity(unzippedFiles)) {
						License downloadedLicense = LicenseUtil.getLicense(unzippedFiles);
						return loadLicenseForAuth(downloadedLicense);
					} else {
						log.error("Unzipping file {} didn't result in correct amount of files!", licensePath);
					}
				} else {
					log.error("No usable license found in path {}", licensePath);
				}
			} else {
				log.error("Specified path {} doesn't contain a folder nor a file", licensePath);
			}
		} catch (IOException | InvalidKeyException | SignatureException | NoSuchAlgorithmException | InvalidKeySpecException ioe) {
			log.error("Couldn't load license from path because {}", ioe.getMessage());
		}
		return license;
	}

	public CompanyLicensePublic loadLicenseForAuth(License license) {
		if (checkLicenseProps(license)) {
			return createLicenseForAuth(license);
		} else {
			log.error("The required license properties couldn't be obtained from the provided license.");
		}
		return null;

	}

	/**
	 * Checks if the properties in the license object are set (not null or empty).
	 *
	 * @param license
	 *          The {@link License} for which the properties are checked.
	 * @return True if the properties in the license object are set, false if only one property is null or empty
	 */
	private boolean checkLicenseProps(License license) {
		Boolean isLicensePropsValid = false;

		if (license == null) {
			return isLicensePropsValid;
		}

		if (!Strings.isNullOrEmpty(license.getProperty("groupId")) && !Strings.isNullOrEmpty(license.getProperty("licenseId"))
				&& !Strings.isNullOrEmpty(license.getExpirationDateAsString())) {
			if (!LicenseDateUtil.isLicenseExpired(license.getExpirationDateAsString())) {
				isLicensePropsValid = true;
			}
		}
		return isLicensePropsValid;
	}

	/**
	 * Tries to activate the provided {@link CompanyLicensePublic} via the Portal API. If successful the current access token is obtained and
	 * stored in the {@link ProbeConfiguration}.
	 *
	 * @param license
	 *          The license which should be used for activation.
	 * @return
	 */
	public boolean authenticateLicense() {
		CompanyLicensePublic license = loadLicenseFromDB();
		if (license != null) {
			String response = RESTUtils.sendPost(LoadedConfigItems.getInstance().getLicenseAPI() + "/authenticate", license);
			if (response != null) {
				license = activateLicenseAndUpdateInDB(response, license);
				if (license != null) {
					ProbeConfiguration.authKey = license.getAccessToken();
					ProbeConfiguration.probeId = license.getDeviceId();
					ProbeConfiguration.groupId = license.getGroupId();
					ProbeConfiguration.setAPIAvailablitity(true);
					log.info("License successfully activated and AuthToken obtained");
					return true;
				} else {
					log.error("A problem occured while activating the license in DB.");
				}
			} else {
				log.error("A problem occured while loading the license from path.");
			}
		}
		return false;
	}

	/**
	 * Creates a {@link CompanyLicensePublic} from the provided {@link License}. It also checks if there is already a license stored in the
	 * DB, if there is a license stored it just updates the license. If there is no license stored in the DB, it creates a new entry in the
	 * DB.
	 *
	 * @param license
	 *          The {@link License} object which should be used for creating a {@link CompanyLicensePublic} for authentication
	 * @return The {@link CompanyLicensePublic} for authentication.
	 *
	 */
	public CompanyLicensePublic createLicenseForAuth(License license) {
		String groupId, licenseId, expirationDate;

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
			ProbeConfiguration.hostname = InetAddress.getLocalHost().getHostName();
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
				ProbeConfiguration.probeId = UUID.randomUUID().toString();
				storedLicense.setDeviceId(ProbeConfiguration.probeId);
			}
			storedLicense.setGroupId(groupId);
			storedLicense.setLicenseId(licenseId);
			storedLicense.setExpirationDate(expirationDate);
		} else {
			ProbeConfiguration.probeId = UUID.randomUUID().toString();
			storedLicense = new CompanyLicensePublic(groupId, licenseId, expirationDate, ProbeConfiguration.probeId);
		}
		storedLicense.setStatus(DeviceStatus.ONLINE);
		storedLicense.setHostname(ProbeConfiguration.hostname);
		/*
		 * Update the license in the local DB.
		 */
		updateLicenseInDB(storedLicense);
		return storedLicense;
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
	private CompanyLicensePublic activateLicenseAndUpdateInDB(String authToken, CompanyLicensePublic license) {
		if (!Strings.isNullOrEmpty(authToken)) {
			CompanyLicensePublic receivedLicense = JSONUtils.fromString(authToken, CompanyLicensePublic.class);
			if (receivedLicense != null) {
				if (!receivedLicense.getDeviceId().equals(license.getDeviceId())) {
					log.error("Received license doesn't contain the same device ID, needs to be verified, continuing for now!");
					return null;
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
		return loadLicenseFromDB();
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

}
