package com.simple2secure.probe.utilities;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.probe.gui.ProbeWorkerThread;

public class ProbeCLI {
	private static Logger log = LoggerFactory.getLogger(ProbeCLI.class);

	public void start() {
	    boolean running = true;

	}
	
	public static void main(String[] args) {
		String importFilePath;

		if (args.length != 2) {
			log.error("Invalid amount of parameters.");
		} else {
			importFilePath = args[1].toString();

			CompanyLicenseObj licenseFile = ProbeUtilities.loadLicenseFromPath(importFilePath);

			if (licenseFile != null) {
				log.debug("The license file was imported succesfully.");
			}
		}

		ProbeWorkerThread workerThread = new ProbeWorkerThread();
		workerThread.run();
	}
}
