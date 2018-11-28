package com.simple2secure.commons.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.license.LicenseUtil;

public class TestLicenseUtil {

	private static Logger log = LoggerFactory.getLogger(TestLicenseUtil.class);

	private static String licenseFilePath = "licenses/";

	private static String privateKeyPath = "licenses/private.key";

	private static String publicKeyPath = "licenses/public.key";

	@BeforeAll
	public static void init() throws Exception {
		licenseFilePath = LicenseUtil.getLicensePath(licenseFilePath);
		publicKeyPath = LicenseUtil.getLicenseKeyPath(publicKeyPath, licenseFilePath);
		privateKeyPath = LicenseUtil.getLicenseKeyPath(privateKeyPath, licenseFilePath);
		LicenseUtil.initialize(licenseFilePath, privateKeyPath, publicKeyPath);
	}

	@Test
	public void testCreateExpiredLicense() throws Exception {
		String licenseId = LicenseUtil.generateLicenseId();
		LicenseUtil.createLicense("testgroup", licenseId, "24/11/2018");
		LicenseUtil.generateLicenseZIPFile(licenseFilePath + licenseId + "\\license.dat", publicKeyPath,
				licenseFilePath + licenseId + "\\license-" + licenseId + ".zip");
	}

}
