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
