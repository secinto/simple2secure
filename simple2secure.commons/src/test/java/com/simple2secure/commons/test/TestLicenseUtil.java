package com.simple2secure.commons.test;

import java.io.File;
import java.security.KeyPair;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.crypto.KeyUtils;
import com.simple2secure.commons.license.LicenseUtil;

public class TestLicenseUtil {

	private static Logger log = LoggerFactory.getLogger(TestLicenseUtil.class);

	private static String licenseFilePath = "licenses" + File.separator;

	private static String privateKeyPath = "private.key";

	private static String publicKeyPath = "public.key";

	@BeforeAll
	public static void init() throws Exception {
		licenseFilePath = LicenseUtil.getLicensePath(licenseFilePath);

		KeyPair ecKeyPair = KeyUtils.generateKeyPair(192);
		File publicKeyFile = KeyUtils.writeKeyToFile(ecKeyPair.getPublic(), licenseFilePath + publicKeyPath);
		File privateKeyFile = KeyUtils.writeKeyToFile(ecKeyPair.getPrivate(), licenseFilePath + privateKeyPath);

		publicKeyPath = publicKeyFile.getAbsolutePath();
		privateKeyPath = privateKeyFile.getAbsolutePath();

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
