package com.simple2secure.probe.license;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.commons.rest.RESTUtils;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.ProbeUtils;

import ro.fortsoft.licensius.License;
import ro.fortsoft.licensius.LicenseNotFoundException;

public class LicenseController {

	private static Logger log = LoggerFactory.getLogger(LicenseController.class);

	private LoadedConfigItems loadedConfigItems = new LoadedConfigItems();

	public LicenseController() {
		init();
	}

	public void init() {
		LicenseUtil.initialize(System.getProperty("user.dir"), "public.key");
	}

	/**
	 * Unzips the directory containing the license.dat in the /simple2secure/probe/ project directory. Further this method maps the License
	 * object to a CompanyLicenseObj(has additionally probeId).
	 *
	 * @param... The path to the license.zip-directory @throws... IOException if a problem occure during the unzipping @throws...
	 * LicenseException if something went wrong while unzipping or the
	 *
	 * @throws LicenseNotFoundException
	 * @throws InterruptedException
	 */
	public CompanyLicensePublic loadLicenseFromPath(String importFilePath) throws Exception {
		CompanyLicensePublic license = null;
		File inputFile = new File(importFilePath);

		if (inputFile != null) {
			List<File> unzippedFiles = ProbeUtils.unzipImportedFile(inputFile);
			if (unzippedFiles != null && unzippedFiles.size() == 2) {
				License downloadedLicense = LicenseUtil.getLicense();

				if (downloadedLicense != null && checkLicenseProps(downloadedLicense)) {
					license = createLicenseForAuth(downloadedLicense);
				}
			} else {
				log.error("Unzipping files didn't result in correct amount of files!");
			}
		}
		return license;
	}

	/**
	 * Checks if there is a license stored in the DB... Checks if the license found in DB is expired... Checks if the license is activated
	 *
	 * @return... String of enum Type "FIRST_TIME" if there is no license stored in the DB @return... String of enum Type "LICENSE_EXPIRED" if
	 * the license is in DB but expired @return... String of enum Type "NOT_ACTIVATED" if the license is not expired but the
	 * isActivated()-flag is not set @return... String of enum Type "VALID_CONDITIONS" if the license is not expired & the isActivated()-flag
	 * is set/ sets the isLicenseValid-flag in ProbeConfiguration to true
	 */
	public StartConditions checkProbeStartConditions() {
		CompanyLicensePublic license = loadLicenseFromDB();
		if (license != null) {
			if (!isLicenseExpired(license)) {
				if (license.isActivated()) {
					ProbeConfiguration.isLicenseValid = true;
					ProbeConfiguration.probeId = license.getProbeId();
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
	 * @param ...License
	 *          object
	 * @return ...true if the properties in the license object are set, false if only one property is null or empty
	 */
	public boolean checkLicenseProps(License license) {
		Boolean isLicensePropsValid = false;
		if (!Strings.isNullOrEmpty(license.getFeature("groupId")) && !Strings.isNullOrEmpty(license.getFeature("licenseId"))
				&& !Strings.isNullOrEmpty(license.getExpirationDateAsString())) {
			isLicensePropsValid = true;
		}
		return isLicensePropsValid;
	}

	/**
	 * Updates the license in the DB with the local license.
	 *
	 * @param ...License
	 *          object
	 */
	public void updateLicenseInDB(CompanyLicensePublic license) {
		DBUtil.getInstance().merge(license);
	}

	// TODO: Possibly null check required
	/**
	 * Loads the license from the data base.
	 *
	 * @return ...a CompanyLicenseObject
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
	 * Sets the needed Flags in the DB to mark the license activated.
	 *
	 * @param license
	 *          ...CompanyLicenseObj
	 * @param authToken
	 *          ...the token which the server returns if the activation succeeded.
	 *
	 */
	public void activateLicenseInDB(String authToken, CompanyLicensePublic license) {
		license.setAccessToken(authToken);
		license.setActivated(true);
		updateLicenseInDB(license);
	}

	// TODO: Possibly null check required
	/**
	 * Creates a CompanyLicenseObj to send it to the server for authentication. Also checks if there is already a license stored in the DB, if
	 * there is a license stored it just updates the license. If there is no license stored in the DB, it creates a new entry in the DB.
	 *
	 * @param ...License
	 *          object
	 * @return ...CompanyLicenseObj for authentication.
	 *
	 */
	public CompanyLicensePublic createLicenseForAuth(License license) {
		String probeId = "";
		String groupId, licenseId, expirationDate;
		CompanyLicensePublic result;

		if (license == null) {
			return null;
		}

		groupId = license.getFeature("groupId");
		licenseId = license.getFeature("licenseId");
		expirationDate = license.getExpirationDateAsString();

		result = loadLicenseFromDB();

		if (result != null) {
			if (Strings.isNullOrEmpty(result.getProbeId())) {
				probeId = UUID.randomUUID().toString();
				result.setProbeId(probeId);
			}
			result.setGroupId(groupId);
			result.setLicenseId(licenseId);
			result.setExpirationDate(expirationDate);
		} else {
			probeId = UUID.randomUUID().toString();
			result = new CompanyLicensePublic(groupId, licenseId, expirationDate, probeId);
		}

		updateLicenseInDB(result);
		return result;
	}

	/**
	 * Checks if a license is expired.
	 *
	 * @return ...true if the license is expired, false if the license is not expired
	 */
	public boolean isLicenseExpired(CompanyLicensePublic license) {
		return System.currentTimeMillis() > ProbeUtils.convertStringtoDate(license.getExpirationDate()).getTime();
	}

	public CompanyLicensePublic checkTokenValidity() {
		CompanyLicensePublic license = loadLicenseFromDB();
		if (license != null) {
			String response = RESTUtils.sendPost(loadedConfigItems.getLicenseAPI() + "/token", license, ProbeConfiguration.authKey);
			if (!Strings.isNullOrEmpty(response)) {
				return JSONUtils.fromString(response, CompanyLicensePublic.class);
			} else {
				return null;
			}
		} else {
			/*
			 * TODO: Create handling if license is not stored in DB.
			 */
			log.error("Couldn't find license in DB. Need to do something here");
			return null;
		}
	}
}
