package com.simple2secure.probe.cli;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.CompanyLicenseObj;
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
		CommandLineParser parser = new DefaultParser();
		ProbeWorkerThread workerThread;
		
		//Create Options Object
		Options options = new Options();
		
		//add --filepath option
		final Option filePath = Option.builder("filepath").required(true).longOpt("filepath").build();
		options.addOption(filePath);
		
		String startConditions = licenseCon.checkProbeStartConditions();
		
		switch(startConditions) {
			case ("FIRST_TIME"):
				if(args.length == 2) {
					try {
						CommandLine line = parser.parse(options, args);
						if(line.getArgs() != null) {
							importFilePath = line.getArgs()[0];
							CompanyLicenseObj licenseFile = null;
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
										workerThread = new ProbeWorkerThread();
										workerThread.run();
										break;
									}
								}
								log.error("A problem occured while loading the license from path.");
							} catch (IOException | LicenseNotFoundException | LicenseException e) {
								log.error("A problem occured while loading the license from path. Concrete exception: {}", e);
							}
						}
					} catch (ParseException e) {
						log.error("You have not started ProbeCLI accordingly.");
					}
				}
				log.error("You have to enter the \"filepath\" in the filepath option.");
				break;
			case ("LICENSE_EXPIRED"): 
				workerThread = new ProbeWorkerThread();
				workerThread.run();
				break;
			case ("NOT_ACTIVATED"): 
				workerThread = new ProbeWorkerThread();
				workerThread.run();
				break;
			case ("VALID_CONDITIONS"): 
				workerThread = new ProbeWorkerThread();
				workerThread.run();
				break;
		}
	}
}
