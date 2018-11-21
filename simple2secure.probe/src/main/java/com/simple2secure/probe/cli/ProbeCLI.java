package com.simple2secure.probe.cli;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.license.LicenseController;
import com.simple2secure.probe.scheduler.ProbeWorkerThread;
import com.simple2secure.probe.utils.RequestHandler;

import ro.fortsoft.licensius.LicenseException;
import ro.fortsoft.licensius.LicenseNotFoundException;

public class ProbeCLI {
	private static Logger log = LoggerFactory.getLogger(ProbeCLI.class);

//	public void start() {
//	    boolean running = true;
//
//	}

	public static void main(String[] args) {
		String importFilePath;
		LicenseController licenseCon = new LicenseController();
		String authToken = null;

		switch (args.length) {
		case 1:
			if (licenseCon.checkProbeStartConditions()) {
				ProbeWorkerThread workerThread = new ProbeWorkerThread();
				workerThread.run();
			}
			break;

		case 2:
			importFilePath = args[1].toString();
			CompanyLicensePublic licenseFile = null;
			try {
				licenseFile = licenseCon.loadLicenseFromPath(importFilePath);
				if (licenseFile != null) {
					authToken = RequestHandler.sendPostReceiveResponse(
							ProbeConfiguration.getInstance().getLoadedConfigItems().getLicenseAPI() + "/activateProbe",
							licenseFile);
					if (authToken != null) {
						licenseCon.activateLicenseInDB(authToken, licenseFile);

						ProbeConfiguration.authKey = authToken;
						ProbeConfiguration.probeId = licenseFile.getProbeId();
						ProbeConfiguration.setAPIAvailablitity(true);
						ProbeWorkerThread workerThread = new ProbeWorkerThread();
						workerThread.run();
						break;
					}
				}
				log.error("A problem occured while loading the license from path.");
			} catch (IOException | LicenseNotFoundException | LicenseException e) {
				log.error("A problem occured while loading the license from path. Concrete exception: {}", e);
			}
			break;

		default:
			log.error(
					"Invalid amount of Arguments. Please pass just the location of your downloaded license dir as argument.");
			break;
		}
	}
}
