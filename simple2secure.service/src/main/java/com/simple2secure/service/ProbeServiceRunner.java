package com.simple2secure.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.commons.file.FileUtil;
import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.commons.process.ProcessUtils;

public class ProbeServiceRunner {

	private static Logger log = LoggerFactory.getLogger(ProbeServiceRunner.class);

	private static String workingDirectory = System.getProperty("user.dir");

	public static void main(String[] args) {
		ProcessContainer startedService = startService("ProbeService", "Probe Service", "simple2secure.service-0.1.0.jar",
				"com.simple2secure.service.ProbeControllerService");

		/*
		 * Check if we can find the license file in the directory, if not start the probe service without the license file.
		 */
		String licenseFile = findLicenseFile();

		if (!Strings.isNullOrEmpty(licenseFile)) {
			ProcessUtils.startProcess("java -jar simple2secure.probe.jar -l " + licenseFile);
		} else {
			log.info("License file is not available, starting service without.");
			ProcessUtils.startProcess("java -jar simple2secure.probe.jar");
		}
	}

	public static String findLicenseFile() {
		String licenseFile = "";
		List<String> fileType = new ArrayList<String>();
		fileType.add(".dat");
		fileType.add(".zip");
		try {
			List<File> foundFiles = FileUtil.getFilesFromDirectory(workingDirectory, true, fileType);
			if (foundFiles != null && !foundFiles.isEmpty()) {
				for (File foundFile : foundFiles) {
					if (foundFile.getName().endsWith("license.dat")) {
						licenseFile = foundFile.getAbsolutePath();
						/*
						 * TODO: Verify if we need to have some backup strategy.
						 */
						break;
					}
				}
			}
		} catch (Exception e) {
			log.error("Searching for license file failed. Reason {}", e);
		}
		return licenseFile;
	}

	public static ProcessContainer startService(String serviceName, String displayName, String jarFile, String startClass) {
		StringBuilder serviceString = new StringBuilder();
		serviceString.append(workingDirectory + "\\daemon\\");
		serviceString.append("prunsrv.exe //IS//");
		serviceString.append(serviceName);
		serviceString.append(" --DisplayName=\"");
		serviceString.append(displayName);
		serviceString.append("\" --Jvm=auto --Classpath=");
		serviceString.append(jarFile);
		serviceString.append(" --StartMode=jvm --StartClass=");
		serviceString.append(startClass);
		serviceString.append(" --Startup=Auto");

		return ProcessUtils.startProcess(serviceString.toString());
	}

}
