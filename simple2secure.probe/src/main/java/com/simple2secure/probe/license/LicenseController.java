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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.file.FileUtil;
import com.simple2secure.commons.file.ZIPUtils;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.commons.license.License;
import com.simple2secure.commons.license.LicenseDateUtil;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.ProbeUtils;
import com.simple2secure.probe.utils.RESTUtils;

public class LicenseController {

	private static Logger log = LoggerFactory.getLogger(LicenseController.class);

	public LicenseController() {
	}

	/**
	 * Loads the license from the database - standard case - or from the file if no license exists in the database or if the license should be
	 * re-authenticated from the one found in the file system.
	 *
	 * @return
	 */
	public CompanyLicensePublic loadLicense() {
		CompanyLicensePublic license = loadLicenseFromDB();
		if (license == null || ProbeConfiguration.reauthenticate) {
			log.debug("License will be loaded from path");
			if (ProbeConfiguration.reauthenticate) {
				log.debug("License will be reauthenticated");
			}
			license = loadLicenseFromPath();
			// ProbeConfiguration.reauthenticate = false;
		}

		if (license != null) {
			ProbeConfiguration.probeId = license.getDeviceId();
			ProbeConfiguration.groupId = license.getGroupId().toStringMongod();
			ProbeConfiguration.authKey = license.getAccessToken();
			ProbeConfiguration.refreshKey = license.getRefreshToken();
		} else {
			log.error("No license could be obtained from database nor file system. Exiting");
			System.err.println("No license could be obtained from database nor file system. Exiting");
			System.exit(-2);
		}
		return license;
	}

	/**
	 * Checks if there is a license stored in the DB. Checks if the license is activated. If not, it is activated and if the activation was
	 * successful {@link StartConditions#LICENSE_VALID} is returned. If the license is expired or the activation was not successful it is
	 * tried to obtain a license from the specified license path. If a valid license is available it is activated and
	 * {@link StartConditions#LICENSE_VALID} is returned. Otherwise {@link StartConditions.LICENSE_NOT_AVAILABLE} is returned.
	 *
	 * @return The {@link StartConditions} which corresponds to the current state.
	 */
	public StartConditions checkLicenseValidity(CompanyLicensePublic license) {
		if (license != null) {
			if (!LicenseDateUtil.isLicenseExpired(license.getExpirationDate())) {
				if (authenticateLicense()) {
					return StartConditions.LICENSE_VALID;
				}
			} else {
				return StartConditions.LICENSE_NOT_VALID;
			}
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
			/*
			 * TODO: Hack because currently it doesn't work if the license has already been activated.
			 */
			if (!license.isActivated() && Strings.isNullOrEmpty(license.getAccessToken()) || ProbeConfiguration.reauthenticate) {
				String response = RESTUtils.sendPost(LoadedConfigItems.getInstance().getBaseURL() + StaticConfigItems.LICENSE_API + "/authenticate",
						license);
				if (ProbeConfiguration.reauthenticate) {
					log.debug("Tried reauthentication");
					ProbeConfiguration.reauthenticate = false;
				}
				if (response != null) {
					license = activateLicenseAndUpdateInDB(response, license);
					if (license != null) {
						ProbeConfiguration.authKey = license.getAccessToken();
						ProbeConfiguration.refreshKey = license.getRefreshToken();
						/*
						 * TODO: Check whether this is really necessary
						 */
						// ProbeConfiguration.probeId = license.getDeviceId();
						// ProbeConfiguration.groupId = license.getGroupId();
						ProbeConfiguration.setAPIAvailablitity(true);
						log.info("License successfully activated and AuthToken obtained");
						return true;
					} else {
						log.error("A problem occured while activating the license in DB.");
					}
				} else {
					log.error("A problem occured while loading the license from path.");
				}
			} else {
				log.info("License activated and access token available, not performing authenticate");
				return true;
			}
		} else {
			log.error("No license found in database, this should never happen because either it is read from file or it is stored in DB.");
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

		/*
		 * Obtain license stored in DB if any.
		 */
		CompanyLicensePublic storedLicense = loadLicenseFromDB();

		/*
		 * If license is already available in DB, update the content with the one provided as input. Otherwise create a new Probe ID and create
		 * a new CompanyLisnecePublic object from it.
		 */
		if (storedLicense != null) {
			if (storedLicense.getDeviceId() != null) {
				storedLicense.setDeviceId(ProbeConfiguration.probeId);
			}
			storedLicense.setGroupId(new ObjectId(groupId));
			storedLicense.setLicenseId(new ObjectId(licenseId));
			storedLicense.setExpirationDate(expirationDate);
		} else {
			ProbeConfiguration.probeId = new ObjectId();
			storedLicense = new CompanyLicensePublic(new ObjectId(groupId), new ObjectId(licenseId), expirationDate, ProbeConfiguration.probeId);
			storedLicense.id = ProbeConfiguration.probeId;
		}

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
				license.setRefreshToken(receivedLicense.getRefreshToken());
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

	/**
	 * Call the api for the renew authentication to retrieve new tokens for the probe usage. If response is empty or license is not provided
	 * in the response, we check if server is available.
	 */
	public void renewAuthentication() {
		CompanyLicensePublic license = loadLicenseFromDB();
		String response = RESTUtils.sendPostRenew(
				LoadedConfigItems.getInstance().getBaseURL() + StaticConfigItems.LICENSE_API + "/renewAuthentication", license, null);

		if (!Strings.isNullOrEmpty(response)) {
			license = activateLicenseAndUpdateInDB(response, license);
			if (license != null) {
				ProbeConfiguration.authKey = license.getAccessToken();
				ProbeConfiguration.refreshKey = license.getRefreshToken();
				ProbeConfiguration.setAPIAvailablitity(true);
			} else {
				ProbeUtils.isServerReachable();
			}
		} else {
			ProbeUtils.isServerReachable();
		}
	}

}
