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
package com.simple2secure.probe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.commons.license.LicenseDateUtil;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.probe.license.LicenseController;
import com.simple2secure.probe.utils.DBUtil;

public class TestLicenseController {
	private LicenseController licenseController = new LicenseController();

	@BeforeAll
	public static void setup() {
		DBUtil.getInstance("s2s-test");
	}

	@Test
	public void testCreateLicenseForAuthFail() {
		CompanyLicensePublic companyLicense = licenseController.createLicenseForAuth(null);
		assertEquals(null, companyLicense);
	}

	@Test
	public void testIsLicenseExpiredFail() throws Exception {

		CompanyLicensePublic companyLicense = new CompanyLicensePublic("test", "1", "10/10/2018", "1");

		boolean result = LicenseDateUtil.isLicenseExpired(companyLicense.getExpirationDate());

		assertEquals(true, result);

	}

	@Test
	public void testLoadLicenseFromDB() {
		CompanyLicensePublic compLicense = new CompanyLicensePublic("1", "2", "3", "12/12/2018");
		licenseController.updateLicenseInDB(compLicense);

		CompanyLicensePublic licenseFrDB;
		licenseFrDB = licenseController.loadLicenseFromDB();

		assertEquals(compLicense.getLicenseId(), licenseFrDB.getLicenseId());
		assertEquals(compLicense.getDeviceId(), licenseFrDB.getDeviceId());
		assertEquals(compLicense.getGroupId(), licenseFrDB.getGroupId());
		assertEquals(compLicense.getExpirationDate(), licenseFrDB.getExpirationDate());
	}

	@Test
	public void testCheckLicenseDirValiditySuccess() {
		List<File> expectedFileList = new ArrayList<>();
		File licenseFile = new File("license.dat");
		File keyFile = new File("public.key");
		expectedFileList.add(licenseFile);
		expectedFileList.add(keyFile);

		boolean result = LicenseUtil.checkLicenseDirValidity(expectedFileList);

		assertEquals(true, result);
	}

	@Test
	public void testCheckLicenseDirValidityFail() {
		List<File> expectedFileList = new ArrayList<>();
		File licenseFile = new File("test.dat");
		File keyFile = new File("public.key");
		expectedFileList.add(licenseFile);
		expectedFileList.add(keyFile);

		boolean result = LicenseUtil.checkLicenseDirValidity(expectedFileList);

		assertEquals(false, result);
	}

}
