package com.simple2secure.probe.utils;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.commons.rest.RESTUtils;
import com.simple2secure.probe.config.ProbeConfiguration;

public class LicenseUtil {

	private static Logger log = LoggerFactory.getLogger(LicenseUtil.class);

	public static boolean validateLicense() {
		/*
		 * Checking if license exists should have already been performed by
		 * configuration loading.
		 */
		CompanyLicensePublic license = checkIfLicenseExists();

		if (license != null) {

			Date expirationDate = ProbeUtils.convertStringtoDate(license.getExpirationDate());
			if (!isLicenseExpired(expirationDate)) {
				ProbeConfiguration.authKey = license.getAccessToken();
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

	public static CompanyLicensePublic checkIfLicenseExists() {
		// LicenseDaoImpl licenseDao = new LicenseDaoImpl();
		CompanyLicensePublic license = getLicenseFromDb();

		CompanyLicensePublic licenseObj = checkTokenValidity();

		if (licenseObj != null) {
			DBUtil.getInstance().merge(licenseObj);
			ProbeConfiguration.authKey = licenseObj.getAccessToken();
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

	public static CompanyLicensePublic getLicenseFromDb() {
		List<CompanyLicensePublic> licenses = DBUtil.getInstance().findAll(new CompanyLicensePublic());

		if (licenses.size() != 1) {
			return null;
		} else {
			return licenses.get(0);
		}

	}

	public static CompanyLicensePublic checkTokenValidity() {
		ProbeConfiguration.isCheckingLicense = true;
		CompanyLicensePublic license = LicenseUtil.getLicenseFromDb();
		if (license != null) {
			String response = RESTUtils.sendPost(
					ProbeConfiguration.getInstance().getLoadedConfigItems().getLicenseAPI() + "/token", license);
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

	private static boolean isLicenseExpired(Date expirationDate) {
		return System.currentTimeMillis() > expirationDate.getTime();
	}
}
