package com.simple2secure.test.portal.token;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.commons.json.JSONUtils;

public class TestCompanyLicense {

	private static Logger log = LoggerFactory.getLogger(TestCompanyLicense.class);

	@Test
	public void testLicenseCasting() {
		CompanyLicensePrivate license = new CompanyLicensePrivate("my group", "l1111", "20/12/2018", false);
		license.setProbeId("dadfas");
		license.setAccessToken("Sasdfasdf");
		license.setTokenSecret("SECRET");

		CompanyLicensePublic publicLicense = license;

		String licenseString = JSONUtils.toString(publicLicense);

		log.info("License string normal casting {}", licenseString);

		assertTrue(licenseString.contains("SECRET"));

		publicLicense = license.getPublicLicense();

		licenseString = JSONUtils.toString(publicLicense);

		log.info("License string after getPublicLicense {}", licenseString);

		assertFalse(licenseString.contains("SECRET"));
	}
}
