package com.simple2secure.test.portal.token;

import org.junit.jupiter.api.Test;

import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.CompanyLicensePublic;

public class TestCompanyLicense {

	@Test
	public void testLicenseCasting() {
		CompanyLicensePrivate license = new CompanyLicensePrivate("my group", false);
		license.setProbeId("dadfas");
		license.setLicenseId("adadf");
		license.setAccessToken("Sasdfasdf");
		license.setTokenSecret("SECRET");

		CompanyLicensePublic publicLicense = (CompanyLicensePublic) license;

	}
}
