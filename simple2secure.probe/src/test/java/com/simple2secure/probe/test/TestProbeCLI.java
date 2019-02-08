package com.simple2secure.probe.test;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.probe.cli.ProbeCLI;

public class TestProbeCLI {

	@Test
	public void testProbeCLIInstrumentationUsingSystemIn() throws IOException {
		File licenseZIP = createLicense();

		String[] args = new String[2];
		args[0] = "-l";
		args[1] = licenseZIP.getAbsolutePath();
		ProbeCLI.main(args);
	}

	private File createLicense() throws IOException {
		LicenseUtil.initialize("src/test/resources/licenses/");
		LicenseUtil.createLicenseFile("testgroup", "1", "18/12/2022");
		String pathOfZipFile = "src/test/resources/licenses" + File.separator + "license.zip";
		LicenseUtil.generateLicenseZIPFromID(pathOfZipFile, "1");
		return new File(pathOfZipFile);

	}
}
