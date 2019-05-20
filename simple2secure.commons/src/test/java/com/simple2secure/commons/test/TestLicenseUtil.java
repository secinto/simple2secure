package com.simple2secure.commons.test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.simple2secure.commons.crypto.KeyUtils;
import com.simple2secure.commons.file.FileUtil;
import com.simple2secure.commons.file.ZIPUtils;
import com.simple2secure.commons.license.License;
import com.simple2secure.commons.license.LicenseUtil;

public class TestLicenseUtil {

	private static String licenseFilePath = "licenses" + File.separator;

	private static String privateKeyPath = "private.key";

	private static String publicKeyPath = "public.key";

	private static String workingDirectory = System.getProperty("user.dir");

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

	@AfterAll
	public static void tearDown() throws IOException, InterruptedException {
		TimeUnit.SECONDS.sleep(1);
		File tempDirectory = new File(licenseFilePath);
		if (tempDirectory.exists() && tempDirectory.isDirectory()) {
			FileUtils.deleteDirectory(tempDirectory);
		}
	}

	@Test
	public void createLicenseFile_licensePropsAsParam_pathToCreatedLicenseFile() throws Exception {
		String licenseFile = LicenseUtil.createLicenseFile("testgroup", "testlicense", "24/11/2018");
		Assertions.assertNotNull(licenseFile);
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
	}

	@Test
	public void getLicensePath_ParamDoNotExist_AbsolutePathOfCreatedDir() throws IOException {
		String path = "notExistingDir" + File.separator;
		String licensePath = LicenseUtil.getLicensePath(path);
		File dirExists = FileUtil.getFile(licensePath);

		Assertions.assertNotNull(dirExists);
		Assertions.assertTrue(dirExists.exists());
		FileUtils.deleteDirectory(dirExists);
	}

	@Test
	public void generateLicenseZIPFile_zipFilePathAsParam_createdLicenseZip() throws IOException {
		LicenseUtil.createLicenseFile("testgroup", "", "24/11/2018");
		String pathOfZipFile = "licenses" + File.separator + "license.zip";
		LicenseUtil.generateLicenseZIPFromFile(pathOfZipFile);
		File zipFile = new File("licenses" + File.separator + "license.zip");
		Assertions.assertNotNull(zipFile);
	}

	@Test
	public void generateLicenseZIPFile_publicKeyAndZipPathAsParam_createdLicenseZip() throws IOException {
		LicenseUtil.createLicenseFile("testgroup", "", "24/11/2018");
		String publicKeyFile = "licenses" + File.separator + "public.key";
		String licenseZipFile = "licenses" + File.separator + "license.zip";
		LicenseUtil.generateLicenseZIPFromFile(publicKeyFile, licenseZipFile);

		File file = new File(licenseZipFile);

		Assertions.assertNotNull(file);
	}

	@Test
	public void generateLicenseZIPFile_LicenseFilePublicKeyZipFileAsParam_createdLicenseZip() throws IOException {
		LicenseUtil.createLicenseFile("testgroup", "", "24/11/2018");
		String publicKeyFile = "licenses" + File.separator + "public.key";
		String licenseZipFile = "licenses" + File.separator + "license.zip";
		LicenseUtil.generateLicenseZIPFromFile("licenses" + File.separator + "license.dat", publicKeyFile, licenseZipFile);

		File file = new File(licenseZipFile);
		Assertions.assertNotNull(file);
	}

	@Test
	public void generateLicenseZIPFile_InvalidLicensePath_ThrowException() throws IOException {
		LicenseUtil.createLicenseFile("testgroup", "", "24/11/2018");
		String publicKeyFile = "licenses" + File.separator + "public.key";
		String licenseZipFile = "licenses" + File.separator + "license.zip";

		Executable closureContainingCodeToTest = () -> {
			LicenseUtil.generateLicenseZIPFromFile("licenses" + File.separator, publicKeyFile, licenseZipFile);
		};
		assertThrows(IOException.class, closureContainingCodeToTest);
	}

	@Test
	public void generateLicenseZIPStream_licenseAsParam_licenseZipStream() throws IOException {
		String licenseFile = LicenseUtil.createLicenseFile("testgroup", "", "24/11/2018");
		ByteArrayOutputStream result = LicenseUtil.generateLicenseZIPStreamFromFile(licenseFile);
		boolean containsLicense = false;
		boolean containsPublickey = false;
		byte[] resultArray = result.toByteArray();

		File importedFile = new File(licenseFile);
		FileUtils.writeByteArrayToFile(importedFile, resultArray);

		List<File> unzippedFiles = ZIPUtils.unzipImportedFile(importedFile);

		for (File file : unzippedFiles) {
			if (file.getName().equals("license.dat")) {
				containsLicense = true;
			}

			if (file.getName().equals("public.key")) {
				containsPublickey = true;
			}
		}
		Assertions.assertEquals(true, containsLicense);
		Assertions.assertEquals(true, containsPublickey);
	}

	@Test
	public void generateLicenseZIPStream_licenseAndPublicKeyAsParam_licenseZipStream() throws IOException {
		LicenseUtil.createLicenseFile("testgroup", "", "24/11/2018");
		File pubKeyFile = new File("public.key");
		File licFile = new File("license.dat");
		File fileToOverwrite = new File(licenseFilePath + "license.dat");
		boolean containsLicense = false;
		boolean containsPublickey = false;
		ByteArrayOutputStream result = LicenseUtil.generateLicenseZIPStreamFromFile(licFile.getPath().toString(),
				pubKeyFile.getPath().toString());
		FileUtils.writeByteArrayToFile(fileToOverwrite, result.toByteArray());

		List<File> unzippedFiles = ZIPUtils.unzipImportedFile(fileToOverwrite);

		for (File file : unzippedFiles) {
			if (file.getName().equals("license.dat")) {
				containsLicense = true;
			}

			if (file.getName().equals("public.key")) {
				containsPublickey = true;
			}
		}
		Assertions.assertEquals(true, containsLicense);
		Assertions.assertEquals(true, containsPublickey);
	}

	@Test
	public void checkLicenseDirValidity_licenseDirAsParam_True() {
		List<File> licenseDir = new ArrayList<>();
		File licFile = new File("license.dat");
		File pubKeyFile = new File("public.key");

		licenseDir.add(licFile);
		licenseDir.add(pubKeyFile);

		Assertions.assertEquals(true, LicenseUtil.checkLicenseDirValidity(licenseDir));
	}

	@Test
	public void checkLicenseDirValidity_invalidLicenseDirAsParam_False() {
		List<File> licenseDir = new ArrayList<>();
		File licFile = new File("license.dat");
		File pubKeyFile = new File("test.key");

		licenseDir.add(licFile);
		licenseDir.add(pubKeyFile);

		Assertions.assertEquals(false, LicenseUtil.checkLicenseDirValidity(licenseDir));
	}
}
