package com.simple2secure.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.probe.license.*;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.ProbeUtils;

import ro.fortsoft.licensius.License;
import ro.fortsoft.licensius.LicenseException;
import ro.fortsoft.licensius.LicenseManager;
import ro.fortsoft.licensius.LicenseNotFoundException;

public class TestLicenseController {
	
	String filePathValidLicense = "K:\\work\\projects\\simple2secure\\simple2secure.probe\\src\\test\\resources\\license-5bed73521dbe9535e0caf2b3.zip";
	String filePathInValidLicense = "K:\\work\\projects\\simple2secure\\simple2secure.probe\\src\\test\\resources\\testLicense.zip";
	CompanyLicensePublic companyLicense;
	CompanyLicensePublic expectedCompanyLicense;
	LicenseController licenseController;
	License license;
	
	@BeforeEach
	public void init() {
		try {
			license = LicenseManager.getInstance().getLicense();
		} catch (LicenseNotFoundException | LicenseException e) {
			e.printStackTrace();
		} 
		expectedCompanyLicense = new CompanyLicensePublic() ;
		licenseController = new LicenseController();
	}

	//In this test the probeId is not being tested
	@Test
	public void testCreateLicenseForAuthSucces() {
		expectedCompanyLicense.setLicenseId(license.getFeature("licenseId"));
		expectedCompanyLicense.setGroupId(license.getFeature("groupId"));
		expectedCompanyLicense.setExpirationDate(license.getExpirationDateAsString());
		
		companyLicense = licenseController.createLicenseForAuth(license);
		
		assertEquals(license.getFeature("licenseId"), companyLicense.getLicenseId());
		assertEquals(license.getFeature("groupId"), companyLicense.getGroupId());
		assertEquals(license.getExpirationDateAsString(), companyLicense.getExpirationDate());
	}
	
	@Test
	public void testCreateLicenseForAuthFail() {
		companyLicense = licenseController.createLicenseForAuth(null);
		assertEquals(null, companyLicense);
	}
	
	@Test
	public void TestIsLicenseExpiredSucces() {
		File file = new File(filePathValidLicense);
		try {
			ProbeUtils.unzipImportedFile(file);
			license = LicenseManager.getInstance().getLicense();
		} catch (IOException | LicenseNotFoundException | LicenseException e) {
			e.printStackTrace();
		}
		companyLicense = new CompanyLicensePublic(license.getFeature("groupId"), "1", license.getFeature("licenseId"), license.getExpirationDateAsString());
		
		boolean result = licenseController.isLicenseExpired(companyLicense);
		
		assertEquals(result, false);
	}
	
	public void TestIsLicenseExpiredFail() {
		File file = new File(filePathInValidLicense);
		try {
			ProbeUtils.unzipImportedFile(file);
			license = LicenseManager.getInstance().getLicense();
		} catch (IOException | LicenseNotFoundException | LicenseException e) {
			e.printStackTrace();
		}
		companyLicense = new CompanyLicensePublic(license.getFeature("groupId"), "1", license.getFeature("licenseId"), license.getExpirationDateAsString());
		
		boolean result = licenseController.isLicenseExpired(companyLicense);
		
		assertEquals(result, true);
	}
	
	@Test
	public void TestActivateLicenseInDB() {
		
	}
	
	@Test
	public void TestLoadLicenseFromDB() {
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
	public void TestUpdateLicenseInDB() {
	}
	
	@Test
	public void TestCheckLicenseProps() {
	}
	
	@Test
	public void TestCheckLicenseDirValiditySuccess() {
		List<File> expectedFileList = new ArrayList<>();
		File licenseFile = new File("license.dat");
		File keyFile = new File("public.key");
		expectedFileList.add(licenseFile);
		expectedFileList.add(keyFile);
		
		boolean result = licenseController.checkLicenseDirValidity(expectedFileList);
		
		assertEquals(true, result);
	}
	
	@Test
	public void TestCheckLicenseDirValidityFail() {
		List<File> expectedFileList = new ArrayList<>();
		File licenseFile = new File("test.dat");
		File keyFile = new File("public.key");
		expectedFileList.add(licenseFile);
		expectedFileList.add(keyFile);
		
		boolean result = licenseController.checkLicenseDirValidity(expectedFileList);
		
		assertEquals(false, result);
	}
	
	@Test
	public void TestLoadLocalLicense() {
	}
	
	@Test
	public void TestCheckLicenseValidity() {
	}
	
	@Test
	public void TestCheckProbeStartConditions() {
	}
	
}
