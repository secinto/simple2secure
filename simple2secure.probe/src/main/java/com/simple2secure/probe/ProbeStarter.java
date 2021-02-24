package com.simple2secure.probe;
/**
 *********************************************************************
 *
 * Copyright (C) 2019 by secinto GmbH (http://www.secinto.com)
 *
 *********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 *********************************************************************
 */

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.simple2secure.probe.cli.ProbeCLI;
import com.simple2secure.probe.config.ProbeConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:stefan.kraxberger@secinto.com">Stefan Kraxberger</a>
 *
 */
@Slf4j
public class ProbeStarter {
	private static String OPTION_FILEPATH_SHORT = "l";
	private static String OPTION_FILEPATH = "licensePath";

	private static String OPTION_INSTRUMENTATION_SHORT = "i";
	private static String OPTION_INSTRUMENTATION = "instrumentation";

	private static String OPTION_REAUTH_LICENSE_SHORT = "r";
	private static String OPTION_REAUTH_LICENSE = "reauth";

	public static void main(String[] args) {
		Options options = new Options();

		Option filePath = Option.builder(OPTION_FILEPATH_SHORT).required(false).hasArg().argName("FILE").longOpt(OPTION_FILEPATH)
				.desc("The path to the license ZIP file which should be used.").build();
		Option instrumentation = Option.builder(OPTION_INSTRUMENTATION_SHORT).required(false).argName("INSTRUMENTATION")
				.longOpt(OPTION_INSTRUMENTATION).desc("Specifies if the PROBE should be started using instrumenation").build();
		Option reauth = Option.builder(OPTION_REAUTH_LICENSE_SHORT).required(false).longOpt(OPTION_REAUTH_LICENSE)
				.desc("The license should be reauthenticated from the provided license path").build();

		options.addOption(filePath);
		options.addOption(instrumentation);
		options.addOption(reauth);
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(options, args);
			ProbeCLI client = new ProbeCLI();

			if (line.hasOption(reauth.getOpt())) {
				log.debug("Initializing PROBE and reauthenticate with provided license path");
				ProbeConfiguration.reauthenticate = true;
			}

			if (line.hasOption(filePath.getOpt())) {
				String licensePath = line.getOptionValue(filePath.getOpt());
				log.debug("Initializing PROBE with provided license path {}", licensePath);
				client.init(licensePath);
			} else {
				log.debug("Initializing PROBE with default license path");
				client.init("./license/");
			}

			if (line.hasOption(instrumentation.getOpt())) {
				log.debug("Starting PROBE with instrumentation");
				client.startInstrumentation();
			} else {
				log.debug("Starting PROBE in normal mode");
				client.startAutonomous();
			}

		} catch (ParseException e) {
			String header = "Start monitoring your system using Probe\n\n";
			String footer = "\nPlease report issues at https://github.com/secinto/simple2secure/issues";
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ProbeCLI", header, options, footer, true);
		}

	}
}
