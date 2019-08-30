package com.simple2secure.service.control;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.service.ServiceUtils;

public class ManageService {

	private static Logger log = LoggerFactory.getLogger(ManageService.class);

	private static String serviceName = "Probe Service";

	private static String startServiceClassName = "com.simple2secure.service.ProbeControllerService";
	private static String stopServiceClassName = "com.simple2secure.service.ProbeControllerService";

	private static String libraryFilename = "libs\\simple2secure.service.jar";
	private static String startMethod = "windowsService";
	private static String stopMethod = "windowsService";

	private static String libraryPath = System.getProperty("user.dir");

	private static String OPTION_ACTION_SHORT = "a";
	private static String OPTION_ACTION = "action";

	public static void main(String args[]) {

		Options options = new Options();

		Option filePath = Option.builder(OPTION_ACTION_SHORT).required(true).hasArg().argName("ACTION").longOpt(OPTION_ACTION).desc(
				"Specifies what kind of service action should be performed. Available are either \"install\",  \"start\",  \"stop\",  \"delete\" ")
				.build();

		options.addOption(filePath);

		String action = "";

		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(filePath.getOpt())) {
				action = line.getOptionValue(filePath.getOpt());
			}

		} catch (ParseException e) {
			String header = "Managing probe as a service\n\n";
			String footer = "\nPlease report issues at https://github.com/secinto/simple2secure/issues";
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ProbeCLI", header, options, footer, true);
		}

		if (!libraryPath.endsWith("release")) {
			libraryPath = libraryPath + "\\release";
		}

		log.info("Using library path {}", libraryPath);

		if (action.equalsIgnoreCase("install")) {
			log.info("Installing service {}", serviceName);
			ServiceUtils.installService(true, libraryPath, serviceName, serviceName, libraryFilename, startServiceClassName, startMethod,
					stopServiceClassName, stopMethod);
		} else if (action.equalsIgnoreCase("start")) {
			log.info("Starting service {}", serviceName);
			ServiceUtils.startService(true, libraryPath, serviceName);
		} else if (action.equalsIgnoreCase("stop")) {
			log.info("Stopping service {}", serviceName);
			ServiceUtils.stopService(true, libraryPath, serviceName);
		} else if (action.equalsIgnoreCase("delete")) {
			log.info("Stopping and removing service {}", serviceName);
			ServiceUtils.stopService(true, libraryPath, serviceName);
			ServiceUtils.deleteService(true, libraryPath, serviceName);
		} else {
			log.info("Provided action is not recognized {}", action);
		}
	}
}
