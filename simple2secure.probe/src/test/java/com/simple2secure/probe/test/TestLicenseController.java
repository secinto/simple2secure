package com.simple2secure.probe.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.probe.license.LicenseController;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.ProbeUtils;

import ro.fortsoft.licensius.License;
import ro.fortsoft.licensius.LicenseGenerator;
import ro.fortsoft.licensius.LicenseManager;
import ro.fortsoft.licensius.OrderedProperties;

public class TestLicenseController {
	public static String licenseFileWrongSignature = "licenses/licenseWrongSignature.dat";
	public static String licenseFileExpiredAndWrongSignature = "licenses/licenseExpiredAndWrongSignature.dat";
	public static String licenseValid = "licenses/licenseOK.dat";

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

		assertEquals(license.getFeature("licenseId"), companyLicense.getLicenseId());
		assertEquals(license.getFeature("groupId"), companyLicense.getGroupId());
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

		CompanyLicensePublic companyLicense = new CompanyLicensePublic(license.getFeature("groupId"), "1", license.getFeature("licenseId"),
				license.getExpirationDateAsString());

		boolean result = licenseController.isLicenseExpired(companyLicense);

		assertEquals(false, result);
	}

	@Test
	public void testIsLicenseExpiredFail() throws Exception {
		License license = getLicense(licenseFileExpiredAndWrongSignature);

		CompanyLicensePublic companyLicense = new CompanyLicensePublic(license.getFeature("groupId"), "1", license.getFeature("licenseId"),
				license.getExpirationDateAsString());

		boolean result = licenseController.isLicenseExpired(companyLicense);

		assertEquals(true, result);

	}

	@Test
	public void testIsLicenseFromZIPExpiredFail() throws Exception {
		License license = getLicenseFromZip(filePathLicenseExpiredWrongSignature);
		assertNotNull(license);

		CompanyLicensePublic companyLicense = new CompanyLicensePublic(license.getFeature("groupId"), "1", license.getFeature("licenseId"),
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
		LicenseManager.LICENSE_FILE = file.getAbsolutePath();
		return LicenseManager.getInstance().getLicense();

	}

	private License getLicenseFromZip(String zipFile) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();

		List<File> files = ProbeUtils.unzipImportedFile(new File(classLoader.getResource(zipFile).getFile()));
		for (File file : files) {
			if (file.getName().contains("license.dat")) {
				return LicenseManager.getInstance().getLicense(file);
			}
		}
		return null;
	}

	private License createLicense(String groupId, String licenseId, String expirationDate) throws Exception {
		Properties properties = new OrderedProperties();
		properties.setProperty("expirationDate", expirationDate);
		properties.setProperty("groupId", groupId);
		properties.setProperty("licenseId", licenseId);
		LicenseGenerator.generateLicense(properties, StaticConfigItems.KEYS_LOCATION + "private.key");
		return LicenseManager.getInstance().getLicense();
	}

}
