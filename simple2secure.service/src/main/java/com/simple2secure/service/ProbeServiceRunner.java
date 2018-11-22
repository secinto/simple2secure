package com.simple2secure.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.file.FileUtil;
import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.commons.service.ServiceUtils;

public class ProbeServiceRunner {

	private static Logger log = LoggerFactory.getLogger(ProbeServiceRunner.class);

	private static String workingDirectory = System.getProperty("user.dir");

	public static void main(String[] args) {
		log.info("Starting ProbeSericeRunner");
		/*
		 * Installing Probe Service. Command line arguments should also be processed.
		 */
		ProcessContainer startedService = ServiceUtils.installService(workingDirectory, "ProbeService", "Probe Service",
				"simple2secure.service-0.1.0.jar", "com.simple2secure.service.ProbeControllerService", "windowsService",
				"com.simple2secure.service.ProbeControllerService", "windowsService");

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

}
