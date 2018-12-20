package com.simple2secure.probe.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.probe.license.LicenseController;
import com.simple2secure.probe.license.StartConditions;
import com.simple2secure.probe.network.packet.CraftPacketsForTests;
import com.simple2secure.probe.scheduler.ProbeWorkerThread;

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

		StartConditions startConditions = licenseController.checkProbeStartConditions();

		CompanyLicensePublic licenseFile = null;

		switch (startConditions) {
		case LICENSE_NOT_AVAILABLE:
			try {
				licenseFile = licenseController.loadLicenseFromPath(importFilePath);
				if (!licenseController.activateLicense(licenseFile)) {
					log.error("A problem occured while activating the license.");
				}
			} catch (Exception e) {
				log.error("A problem occured while loading the license from path. Concrete exception: {}", e);
			}
			break;
		case LICENSE_NOT_ACTIVATED:
			try {
				if (!Strings.isNullOrEmpty(importFilePath)) {
					licenseFile = licenseController.loadLicenseFromPath(importFilePath);
				} else {
					licenseFile = licenseController.loadLicenseFromDB();
				}
				if (!licenseController.activateLicense(licenseFile)) {
					log.error("A problem occured while activating the license.");
				}
			} catch (Exception e) {
				log.error("A problem occured while loading the license from path. Concrete exception: {}", e);
			}
			break;
		case LICENSE_VALID:
			log.info("Found valid license. Starting probe!");
			break;
		case LICENSE_EXPIRED:
			/*
			 * TODO: Insert handling for licenses which are expired for more than a predefined period.
			 */
			log.info("License expired!");
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

		Option filePath = Option.builder(OPTION_FILEPATH_SHORT).required(true).hasArg().argName("FILE").longOpt(OPTION_FILEPATH)
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

			CraftPacketsForTests.craft();

		} catch (ParseException e) {
			String header = "Start monitoring your system using Probe\n\n";
			String footer = "\nPlease report issues at http://simple2secure.com/issues";
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ProbeCLI", header, options, footer, true);
		}

	}
}
