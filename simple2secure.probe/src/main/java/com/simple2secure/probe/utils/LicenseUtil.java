package com.simple2secure.probe.utils;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.probe.config.ProbeConfiguration;

public class LicenseUtil {

	private static Logger log = LoggerFactory.getLogger(LicenseUtil.class);

	private static Gson gson = new Gson();

	public static boolean validateLicense() {
		/*
		 * Checking if license exists should have already been performed by
		 * configuration loading.
		 */
		CompanyLicenseObj license = checkIfLicenseExists();

		if (license != null) {

			Date expirationDate = ProbeUtils.convertStringtoDate(license.getExpirationDate());
			if (!isLicenseExpired(expirationDate)) {
				ProbeConfiguration.authKey = license.getAuthToken();
				ProbeConfiguration.probeId = license.getProbeId();
				ProbeConfiguration.licenseId = license.getLicenseId();

				if (!ProbeConfiguration.isAPIAvailable()) {
					ProbeConfiguration.isLicenseValid = true;
				}

				ProbeConfiguration.getInstance();
			}
		}

		return ProbeConfiguration.isLicenseValid;

	}

	public static CompanyLicenseObj checkIfLicenseExists() {
		// LicenseDaoImpl licenseDao = new LicenseDaoImpl();
		CompanyLicenseObj license = getLicenseFromDb();

		CompanyLicenseObj licenseObj = checkTokenValidity();

		if (licenseObj != null) {
			DBUtil.getInstance().merge(licenseObj);
			ProbeConfiguration.authKey = licenseObj.getAuthToken();
			ProbeConfiguration.isLicenseValid = true;
			ProbeConfiguration.isCheckingLicense = false;
		} else {
			/// Delete license object from the db and change to the license import view!
			license = LicenseUtil.getLicenseFromDb();
			DBUtil.getInstance().delete(license);
			ProbeConfiguration.isLicenseValid = false;
		}

		if (license != null) {
			if (license.isActivated()) {
				// String licenseExists = APIUtils.sendPostWithResponse(ConfigItems.license_api,
				// license.id);

				return license;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static CompanyLicenseObj getLicenseFromDb() {
		List<CompanyLicenseObj> licenses = DBUtil.getInstance().findAll(new CompanyLicenseObj());

		if (licenses.size() != 1) {
			return null;
		} else {
			return licenses.get(0);
		}

	}

	public static CompanyLicenseObj checkTokenValidity() {
		ProbeConfiguration.isCheckingLicense = true;
		CompanyLicenseObj license = LicenseUtil.getLicenseFromDb();
		if (license != null) {
			String response = APIUtils.sendPostWithResponse(
					ProbeConfiguration.getInstance().getLoadedConfigItems().getLicenseAPI() + "/token", license);
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

	private static boolean isLicenseExpired(Date expirationDate) {
		return System.currentTimeMillis() > expirationDate.getTime();
	}
}
