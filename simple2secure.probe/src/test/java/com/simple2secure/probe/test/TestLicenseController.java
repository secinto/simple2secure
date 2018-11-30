package com.simple2secure.probe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.commons.file.ZIPUtils;
import com.simple2secure.commons.license.License;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.probe.license.LicenseController;
import com.simple2secure.probe.utils.DBUtil;

public class TestLicenseController {
	public static String licenseFileWrongSignature = "licenses/licenseWrongSignature.dat";
	public static String licenseFileExpiredAndWrongSignature = "licenses/licenseExpiredAndWrongSignature.dat";
	public static String licenseValid = "licenses/licenseOK.dat";
	public static String publicKey = "licenses/public.key";

	public static String filePathLicenseValid = "licenses/licenseOK.zip";
	public static String filePathLicenseWrongSignature = "licenses/licenseWrongSignature.zip";
	public static String filePathLicenseExpiredWrongSignature = "licenses/licenseExpiredAndWrongSignature.zip";
	private LicenseController licenseController = new LicenseController();

	@BeforeAll
	public static void setup() {
		DBUtil.getInstance("s2s-test");
	}

	@Test
	public void testCreateLicenseForAuthSuccess() throws Exception {
		License license = getLicense(licenseValid);

		CompanyLicensePublic companyLicense = licenseController.createLicenseForAuth(license);

		assertEquals(license.getProperty("licenseId"), companyLicense.getLicenseId());
		assertEquals(license.getProperty("groupId"), companyLicense.getGroupId());
		assertEquals(license.getExpirationDateAsString(), companyLicense.getExpirationDate());
	}

	@Test
	public void testCreateLicenseForAuthFail() {
		CompanyLicensePublic companyLicense = licenseController.createLicenseForAuth(null);
		assertEquals(null, companyLicense);
	}

	@Test
	public void testIsLicenseFromZIPExpiredSuccess() throws Exception {
		License license = getLicenseFromZip(filePathLicenseValid);

		assertNotNull(license);
		CompanyLicensePublic companyLicense = new CompanyLicensePublic(license.getProperty("groupId"), "1", license.getProperty("licenseId"),
				license.getExpirationDateAsString());

		boolean result = licenseController.isLicenseExpired(companyLicense);

		assertEquals(false, result);
	}

	@Test
	public void testIsLicenseExpiredFail() throws Exception {
		License license = getLicense(licenseFileExpiredAndWrongSignature);

		CompanyLicensePublic companyLicense = new CompanyLicensePublic(license.getProperty("groupId"), "1", license.getProperty("licenseId"),
				license.getExpirationDateAsString());

		boolean result = licenseController.isLicenseExpired(companyLicense);

		assertEquals(true, result);

	}

	@Test
	public void testIsLicenseFromZIPExpiredFail() throws Exception {
		License license = getLicenseFromZip(filePathLicenseExpiredWrongSignature);
		assertNotNull(license);

		CompanyLicensePublic companyLicense = new CompanyLicensePublic(license.getProperty("groupId"), "1", license.getProperty("licenseId"),
				license.getExpirationDateAsString());

		boolean result = licenseController.isLicenseExpired(companyLicense);

		assertEquals(true, result);
	}

	@Test
	public void testActivateLicenseInDB() {

	}

	@Test
	public void testLoadLicenseFromDB() {
		CompanyLicensePublic compLicense = new CompanyLicensePublic("1", "2", "3", "12/12/2018");
		DBUtil.getInstance().merge(compLicense);

		CompanyLicensePublic licenseFrDB;
		licenseFrDB = licenseController.loadLicenseFromDB();

		assertEquals(compLicense.getLicenseId(), licenseFrDB.getLicenseId());
		assertEquals(compLicense.getProbeId(), licenseFrDB.getProbeId());
		assertEquals(compLicense.getGroupId(), licenseFrDB.getGroupId());
		assertEquals(compLicense.getExpirationDate(), licenseFrDB.getExpirationDate());
	}

	@Test
	public void testUpdateLicenseInDB() {
	}

	@Test
	public void testCheckLicenseProps() {
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

	@Test
	public void testLoadLocalLicense() {
	}

	@Test
	public void testCheckLicenseValidity() {
	}

	@Test
	public void testCheckProbeStartConditions() {
	}

	private License getLicense(String license) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(license).getFile());
		File publicKeyFile = new File(classLoader.getResource(publicKey).getFile());
		return LicenseUtil.getLicense(file.getAbsolutePath(), publicKeyFile.getAbsolutePath());

	}

	private License getLicenseFromZip(String zipFileName) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File zipFile = new File(classLoader.getResource(zipFileName).getFile());
		List<File> files = ZIPUtils.unzipImportedFile(zipFile);
		String licenseFile = null;
		String publicKeyFile = null;
		for (File file : files) {
			if (file.getName().contains("license.dat")) {
				licenseFile = file.getAbsolutePath();
			}
			if (file.getName().contains("public.key")) {
				publicKeyFile = file.getAbsolutePath();
			}
		}
		if (!Strings.isNullOrEmpty(licenseFile) && !Strings.isNullOrEmpty(publicKeyFile)) {
			return LicenseUtil.getLicense(licenseFile, publicKeyFile);
		}

		return null;
	}
}
