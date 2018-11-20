package com.simple2secure.probe.license;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.ProbeUtils;
import com.simple2secure.probe.utils.RequestHandler;

import ro.fortsoft.licensius.License;
import ro.fortsoft.licensius.LicenseException;
import ro.fortsoft.licensius.LicenseManager;
import ro.fortsoft.licensius.LicenseNotFoundException;

public class LicenseController {

	private static Logger log = LoggerFactory.getLogger(LicenseController.class);

	private static Gson gson = new Gson();
	private LoadedConfigItems loadedConfigItems = new LoadedConfigItems();

	/**
	 * Unzips the directory containing the license.dat in the /simple2secure/probe/
	 * project directory. Further this method maps the License object to a
	 * CompanyLicenseObj(has additionally probeId).
	 *
	 * @param... The path to the license.zip-directory @throws... IOException if a
	 * problem occure during the unzipping @throws... LicenseException if something
	 * went wrong while unzipping or the
	 * 
	 * @throws LicenseNotFoundException
	 * @throws InterruptedException
	 */
	public CompanyLicenseObj loadLicenseFromPath(String importFilePath)
			throws IOException, LicenseNotFoundException, LicenseException {
		CompanyLicenseObj license = null;
		File inputFile = new File(importFilePath);

		if (inputFile != null) {
			List<File> unzippedFiles = ProbeUtils.unzipImportedFile(inputFile);
			if (unzippedFiles != null && unzippedFiles.size() == 2) {
				License downloadedLicense = LicenseManager.getInstance().getLicense();

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
	 * Checks if there is a license stored in the DB... Checks if the license found
	 * in DB is expired... Checks if the license is activated
	 * 
	 * @return... "False" if there is no license stored in the DB @return... "True"
	 * if the license is in DB but expired @return... "True" if the license is not
	 * expired but the isActivated()-flag is not set @return... "True" if the
	 * license is not expired & the isActivated()-flag is set/ sets the
	 * isLicenseValid-flag in ProbeConfiguration to true
	 */
	public boolean checkProbeStartConditions() {
		CompanyLicenseObj license = loadLicenseFromDB();
		boolean isProbeStartConditionsValid = false;
		if (license != null) {
			if (!isLicenseExpired(license)) {
				if (license.isActivated()) {
					ProbeConfiguration.isLicenseValid = true;
					isProbeStartConditionsValid = true;
				}
				isProbeStartConditionsValid = true;
			}
			isProbeStartConditionsValid = true;
		}
		return isProbeStartConditionsValid;
	}

	/**
	 * Checks the validity of the license.
	 *
	 * @param ...License object
	 * @return ...Boolean isLicenseValid
	 * @throws ...LicenseException
	 * @throws ...LicenseNotFoundException
	 */
	public boolean checkLicenseValidity(License license) throws LicenseNotFoundException, LicenseException {
		return LicenseManager.getInstance().isValidLicense(license);
	}

	/**
	 * Loads the license from the root directory of the project.
	 *
	 * @param
	 * @return ...true if the license is valid, false if it is not valid
	 * @throws ...LicenseException
	 * @throws ...LicenseNotFoundException
	 */
	public License loadLocalLicense() throws LicenseNotFoundException, LicenseException {
		return LicenseManager.getInstance().getLicense();
	}

	/**
	 * Checks if the license directory contains the right files.
	 *
	 * @param ...List<File> licenseDir
	 * @return ...true if the directory contains the right files, false if it does
	 *         not contain the right files
	 */
	public boolean checkLicenseDirValidity(List<File> licenseDir) {
		boolean isValidLicenseDir = false;
		boolean licenseFile = false;
		boolean publicKeyFile = false;

		for (File file : licenseDir) {
			if (file.getName().equals("license.dat")) {
				licenseFile = true;
			} else if (file.getName().equals("public.key")) {
				publicKeyFile = true;
			}
		}

		if (licenseFile && publicKeyFile) {
			isValidLicenseDir = true;
		}

		return isValidLicenseDir;
	}

	/**
	 * Checks if the properties in the license object are set (not null or empty).
	 *
	 * @param ...License object
	 * @return ...true if the properties in the license object are set, false if
	 *         only one property is null or empty
	 */
	public boolean checkLicenseProps(License license) {
		Boolean isLicensePropsValid = false;
		if (!Strings.isNullOrEmpty(license.getFeature("groupId"))
				&& !Strings.isNullOrEmpty(license.getFeature("licenseId"))
				&& !Strings.isNullOrEmpty(license.getExpirationDateAsString())) {
			isLicensePropsValid = true;
		}
		return isLicensePropsValid;
	}

	/**
	 * Updates the license in the DB with the local license.
	 *
	 * @param ...License object
	 */
	public void updateLicenseInDB(CompanyLicenseObj license) {
		DBUtil.getInstance().merge(license);
	}

	// TODO: Possibly null check required
	/**
	 * Loads the license from the data base.
	 * 
	 * @return ...a CompanyLicenseObject
	 */
	public CompanyLicenseObj loadLicenseFromDB() {
		List<CompanyLicenseObj> licenses = DBUtil.getInstance().findAll(new CompanyLicenseObj());

		if (licenses.size() != 1) {
			return null;
		} else {
			return licenses.get(0);
		}
	}

	/**
	 * Sets the needed Flags in the DB to mark the license activated.
	 *
	 * @param license   ...CompanyLicenseObj
	 * @param authToken ...the token which the server returns if the activation
	 *                  succeeded.
	 *
	 */
	public void activateLicenseInDB(String authToken, CompanyLicenseObj license) {
		license.setAuthToken(authToken);
		license.setActivated(true);
		updateLicenseInDB(license);
	}

	// TODO: Possibly null check required
	/**
	 * Creates a CompanyLicenseObj to send it to the server for authentication. Also
	 * checks if there is already a license stored in the DB, if there is a license
	 * stored it just updates the license. If there is no license stored in the DB,
	 * it creates a new entry in the DB.
	 *
	 * @param ...License object
	 * @return ...CompanyLicenseObj for authentication.
	 *
	 */
	public CompanyLicenseObj createLicenseForAuth(License license) {
		String probeId = "";
		String groupId, licenseId, expirationDate;
		CompanyLicenseObj result;

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
			result = new CompanyLicenseObj(groupId, probeId, licenseId, expirationDate);
		}

		updateLicenseInDB(result);
		return result;
	}

	/**
	 * Checks if a license is expired.
	 * 
	 * @return ...true if the license is expired, false if the license is not
	 *         expired
	 */
	public boolean isLicenseExpired(CompanyLicenseObj license) {
		return System.currentTimeMillis() > ProbeUtils.convertStringtoDate(license.getExpirationDate()).getTime();
	}

	public CompanyLicenseObj checkTokenValidity() {
		CompanyLicenseObj license = loadLicenseFromDB();
		if (license != null) {
			String response = RequestHandler.sendPostReceiveResponse(loadedConfigItems.getLicenseAPI() + "/token",
					license);
			if (!Strings.isNullOrEmpty(response)) {
				return gson.fromJson(response, CompanyLicenseObj.class);
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
