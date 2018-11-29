package com.simple2secure.probe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.security.KeyPair;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.commons.crypto.KeyUtils;
import com.simple2secure.commons.license.License;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.commons.test.TestLicenseUtil;
import com.simple2secure.probe.license.LicenseController;
import com.simple2secure.probe.utils.DBUtil;

public class TestLicenseController {

	private LicenseController licenseController = new LicenseController();

	private static Logger log = LoggerFactory.getLogger(TestLicenseUtil.class);

	private static String licenseFilePath = "licenses" + File.separator;

	private static String privateKeyPath = "private.key";

	private static String publicKeyPath = "public.key";

	private static License license;

	@BeforeAll
	public static void init() throws Exception {
		licenseFilePath = LicenseUtil.getLicensePath(licenseFilePath);

		KeyPair ecKeyPair = KeyUtils.generateKeyPair(192);
		File publicKeyFile = KeyUtils.writeKeyToFile(ecKeyPair.getPublic(), licenseFilePath + publicKeyPath);
		File privateKeyFile = KeyUtils.writeKeyToFile(ecKeyPair.getPrivate(), licenseFilePath + privateKeyPath);

		publicKeyPath = publicKeyFile.getAbsolutePath();
		privateKeyPath = privateKeyFile.getAbsolutePath();

		LicenseUtil.initialize(licenseFilePath, privateKeyPath, publicKeyPath);

		DBUtil.getInstance("s2s-test");
		license = LicenseUtil.createLicense("test", LicenseUtil.generateLicenseId(), "11/20/2018");
	}

	@Test
	public void testCreateLicenseForAuthSuccess1() throws Exception {

		CompanyLicensePublic companyLicense = licenseController.createLicenseForAuth(license);

		assertEquals(license.getProperty("licenseId"), companyLicense.getLicenseId());
		assertEquals(license.getProperty("groupId"), companyLicense.getGroupId());
		assertEquals(license.getExpirationDateAsString(), companyLicense.getExpirationDate());
	}

	@Test
	public void testCreateLicenseForAuthFail1() {
		CompanyLicensePublic companyLicense = licenseController.createLicenseForAuth(null);
		assertEquals(null, companyLicense);
	}

	@Test
	public void testFailIsLicenseExpired() throws Exception {

		CompanyLicensePublic companyLicense = new CompanyLicensePublic("test", "1", "11/20/2018", "1");

		boolean result = licenseController.isLicenseExpired(companyLicense);

		assertEquals(true, result);
	}

	@Test
	public void testUpdateLicenseInDB() {
		CompanyLicensePublic compLicense = new CompanyLicensePublic("1", "2", "12/12/2018", "3");
		licenseController.updateLicenseInDB(compLicense);

		CompanyLicensePublic licenseFrDB = licenseController.loadLicenseFromDB();

		assertEquals(compLicense.getLicenseId(), licenseFrDB.getLicenseId());
		assertEquals(compLicense.getProbeId(), licenseFrDB.getProbeId());
		assertEquals(compLicense.getGroupId(), licenseFrDB.getGroupId());
		assertEquals(compLicense.getExpirationDate(), licenseFrDB.getExpirationDate());
	}
}
