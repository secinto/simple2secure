package com.simple2secure.probe.cli;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.license.LicenseController;
import com.simple2secure.probe.license.StartConditions;
import com.simple2secure.probe.scheduler.ProbeWorkerThread;
import com.simple2secure.probe.utils.RequestHandler;

import ro.fortsoft.licensius.LicenseException;
import ro.fortsoft.licensius.LicenseNotFoundException;

public class ProbeCLI {
	private static Logger log = LoggerFactory.getLogger(ProbeCLI.class);

	private static String OPTION_FILEPATH_SHORT = "l";
	private static String OPTION_FILEPATH = "licensePath";

	/**
	 * Initializes the ProbeCLI with importFilePath which specifies the location of the license which should be used to activate this Probe
	 * instance.
	 *
	 * @param importFilePath
	 *          The absolute file path to the License ZIP File.
	 */
	public void init(String importFilePath) {
		LicenseController licenseController = new LicenseController();
		String authToken = null;

		StartConditions startConditions = licenseController.checkProbeStartConditions();

		switch (startConditions) {
		case LICENSE_NOT_AVAILABLE:
			CompanyLicensePublic licenseFile = null;
			try {
				licenseFile = licenseController.loadLicenseFromPath(importFilePath);
				if (licenseFile != null) {
					authToken = RequestHandler.sendPostReceiveResponse(
							ProbeConfiguration.getInstance().getLoadedConfigItems().getLicenseAPI() + "/activateProbe", licenseFile);
					if (authToken != null) {
						licenseController.activateLicenseInDB(authToken, licenseFile);

						ProbeConfiguration.authKey = authToken;
						ProbeConfiguration.probeId = licenseFile.getProbeId();
						ProbeConfiguration.setAPIAvailablitity(true);
						break;
					}
				}
				log.error("A problem occured while loading the license from path.");
			} catch (Exception e) {
				log.error("A problem occured while loading the license from path. Concrete exception: {}", e);
			}
			break;
		case LICENSE_NOT_ACTIVATED:
		case LICENSE_VALID:
		case LICENSE_EXPIRED:
			break;
		}
	}

	/**
	 * Starts the Probe itself. Hope the best.
	 */
	public void start() {
		ProbeWorkerThread workerThread = new ProbeWorkerThread();
		workerThread.run();
	}

	public static void main(String[] args) {
		Options options = new Options();

		Option filePath = Option.builder(OPTION_FILEPATH_SHORT).required(true).argName("ZIP-FILE").longOpt(OPTION_FILEPATH)
				.desc("The path to the license ZIP file which should be used.").build();

		options.addOption(filePath);
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(options, args);
			ProbeCLI client = new ProbeCLI();

			if (line.hasOption(filePath.getOpt())) {
				client.init(line.getOptionValue(filePath.getOpt()));
			}

			client.start();
		} catch (ParseException e) {
			String header = "Start monitoring your system using Probe\n\n";
			String footer = "\nPlease report issues at http://simple2secure.com/issues";
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ProbeCLI", header, options, footer, true);
		}

	}
}
