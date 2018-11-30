package com.simple2secure.commons.test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyPair;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.simple2secure.commons.crypto.KeyUtils;
import com.simple2secure.commons.file.FileUtil;
import com.simple2secure.commons.license.License;
import com.simple2secure.commons.license.LicenseUtil;

public class TestLicenseUtil {

	private static String licenseFilePath = "licenses" + File.separator;

	private static String privateKeyPath = "private.key";

	private static String publicKeyPath = "public.key";

	private static String workingDirectory = System.getProperty("user.dir");

	private final static String licenseFileDir = "licenses" + File.separator;

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
		String licenseFile = LicenseUtil.createLicenseFile("testgroup", licenseId, "24/11/2018");
		Assertions.assertNotNull(licenseFile);
		LicenseUtil.generateLicenseZIPFile(licenseFile, publicKeyPath, licenseFilePath);
	}

	@Test
	public void getLicense_LicenseFilePathAndPublicKeyFileAsParams_LicenseObject() throws Exception {
		Executable closureContainingCodeToTest = () -> {
			LicenseUtil.getLicense(licenseFilePath, "public.key");
		};
		assertThrows(FileNotFoundException.class, closureContainingCodeToTest);
	}

	@Test
	public void getLicense_ParamsValidParams_LicenseFile() throws Exception {
		String licenseFile = LicenseUtil.createLicenseFile("test", "test", "12/31/2018");
		License license = LicenseUtil.getLicense(licenseFile);
		Assertions.assertNotNull(license);
		license = LicenseUtil.getLicense(licenseFile, publicKeyPath);
		Assertions.assertNotNull(license);
	}

	@Test
	public void getLicensePath_ParamIsDir_AbsolutePathOfDir() {
		String licensePath = LicenseUtil.getLicensePath(licenseFilePath);
		Assertions.assertEquals(workingDirectory + File.separator + licenseFileDir, licensePath);
	}

	@Test
	public void getLicensePath_ParamDoNotExist_AbsolutePathOfCreatedDir() throws IOException {
		String path = "notExistingDir" + File.separator;
		String licensePath = LicenseUtil.getLicensePath(path);
		File dirExists = FileUtil.getFile(licensePath);

		Assertions.assertNotNull(dirExists);

		FileUtil.deleteFolder(dirExists.getAbsolutePath());
	}
}
