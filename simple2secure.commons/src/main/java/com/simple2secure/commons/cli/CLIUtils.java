package com.simple2secure.commons.cli;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLIUtils {
	private static Logger log = LoggerFactory.getLogger(CLIUtils.class);

	public static String LICENSE_OPTION = "license";

	public static Option generateLicenseOption() {

		final Option licenseOption = Option.builder("l").required(false).longOpt(LICENSE_OPTION).hasArg(true).desc("License to be processed.")
				.build();
		return licenseOption;
	}

	public static Options appendLicenseOptions(Options options) {
		if (options == null) {
			options = new Options();
		}

		options.addOption(generateLicenseOption());
		return options;
	}

	public static CommandLine generateCommandLine(final Options options, final String[] commandLineArguments) {

		final CommandLineParser cmdLineParser = new DefaultParser();

		CommandLine commandLine = null;

		try {
			commandLine = cmdLineParser.parse(options, commandLineArguments);
		} catch (ParseException parseException) {
			log.error("Unable to parse command-line arguments {} due to {} ", Arrays.toString(commandLineArguments), parseException);
			System.out
					.println("ERROR: Unable to parse command-line arguments " + Arrays.toString(commandLineArguments) + " due to: " + parseException);
		}

		return commandLine;
	}

}
