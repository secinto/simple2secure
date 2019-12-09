/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
package com.simple2secure.probe.cli;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.pcap4j.core.PcapNetworkInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.DeviceStatus;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.file.FileUtil;
import com.simple2secure.commons.security.TLSConfig;
import com.simple2secure.commons.service.ServiceCommand;
import com.simple2secure.commons.service.ServiceCommands;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.license.LicenseController;
import com.simple2secure.probe.license.StartConditions;
import com.simple2secure.probe.scheduler.ProbeWorkerThread;
import com.simple2secure.probe.utils.PcapUtil;
import com.simple2secure.probe.utils.ProbeUtils;

public class ProbeCLI {
	private static Logger log = LoggerFactory.getLogger(ProbeCLI.class);

	private static String OPTION_FILEPATH_SHORT = "l";
	private static String OPTION_FILEPATH = "licensePath";

	private static String OPTION_INSTRUMENTATION_SHORT = "i";
	private static String OPTION_INSTRUMENTATION = "instrumentation";
	private ProbeWorkerThread workerThread;

	/**
	 * Initializes the ProbeCLI with importFilePath which specifies the location of the license which should be used to activate this Probe
	 * instance.
	 *
	 * @param importFilePath
	 *          The absolute file path to the License ZIP File.
	 */
	public void init(String importFilePath) {

		ProbeConfiguration.licensePath = importFilePath;

		try {
			PcapNetworkInterface netwInt = PcapUtil.getNetworkInterfaceByInetAddr(PcapUtil.getIpAddrOfNetworkInterface());
			ProbeConfiguration.hostname = InetAddress.getLocalHost().getHostName();
			ProbeConfiguration.netmask = netwInt.getAddresses().get(1).getNetmask().toString();
			ProbeConfiguration.ipAddress = netwInt.getAddresses().get(1).getAddress().toString();

		} catch (Exception e) {
			log.error("Couldn't obtain network information for machine.");
		}

		TLSConfig.initializeTLSConfiguration(LoadedConfigItems.getInstance().getTrustedCertificates());

		ProbeUtils.isServerReachable();

		LicenseController licenseController = new LicenseController();

		StartConditions startConditions = licenseController.checkLicenseValidity();

		ProbeUtils.saveDeviceInfo(new DeviceInfo(ProbeConfiguration.probeId, ProbeConfiguration.hostname, ProbeConfiguration.ipAddress,
				ProbeConfiguration.netmask, DeviceStatus.ONLINE));

		try {
			prepareOsQuery();
		} catch (IOException e) {
			log.error("OSQuery couldn't be prepared. Stopping execution");
			System.exit(-1);
		}

		switch (startConditions) {
		case LICENSE_NOT_AVAILABLE:
			log.error("A problem occured while updating the license.");
			break;
		default:
			log.info("A valid license is available");
			break;
		}

		workerThread = new ProbeWorkerThread();
	}

	private void stopWorkerThreads() {

	}

	private void startWorkerThreads() {
		/*
		 * Starting background worker threads.
		 */
		workerThread.run();

	}

	private void checkStatus() {
		log.debug("Checking PROBE status");
		if (workerThread != null && workerThread.isRunning()) {
			log.debug("PROBE_STATUS: OK");
			System.out.println(ServiceCommands.CHECK_STATUS.getPositiveCommandResponse());
		} else {
			log.debug("PROBE_STATUS: NOK");
			System.out.println(ServiceCommands.CHECK_STATUS.getNegativeCommandResponse());
		}

	}

	private void prepareOsQuery() throws IOException {
		if (!FileUtil.fileOrFolderExists("tools")) {
			FileUtil.createFolder("tools", true);
		}
		for (String location : StaticConfigItems.OSQUERY_DATA_LOCALTION) {
			File newLocation = new File("./tools" + location);
			if (!newLocation.exists()) {
				FileUtils.copyInputStreamToFile(getClass().getResourceAsStream(location), newLocation);
			}
			if (location.endsWith("osqueryi.exe")) {
				ProbeConfiguration.osQueryExecutablePath = newLocation.getAbsolutePath();
			} else {
				ProbeConfiguration.osQueryConfigPath = newLocation.getAbsolutePath();
			}
		}
	}

	/**
	 * Starts the Probe itself. Hope the best.
	 */
	public void startInstrumentation() {
		boolean running = true;
		/*
		 * Starting instrumentation listening to obtain commands from the ProbeControllerService.
		 */
		Scanner commandService = new Scanner(System.in);
		try {
			ServiceCommand command = ServiceCommand.fromString(commandService.nextLine());
			log.info("Received command {} via instrumentation", command.getCommand().name());
			while (running) {
				switch (command.getCommand()) {
				case START:
					startWorkerThreads();
					break;
				case GET_VERSION:
				case RESET:
					stopWorkerThreads();
					startWorkerThreads();
					break;
				case STOP:
				case TERMINATE:
				case CHECK_STATUS:
					checkStatus();
					break;
				case OTHER:
					log.debug("Obtained not recognized command {}", command);
					stopWorkerThreads();
					running = false;
					break;
				default:
					break;
				}
				command = ServiceCommand.fromString(commandService.nextLine());
				log.debug("Command {} received and starting to process it", command);
			}
			log.info("Exit command {} received from probe controller service. Exiting", command);
		} catch (Exception e) {
			log.error("Receiving instrumentation command failed. Reason {}", e);
		} finally {
			commandService.close();
		}
	}

	public static void main(String[] args) {
		Options options = new Options();

		Option filePath = Option.builder(OPTION_FILEPATH_SHORT).required(false).hasArg().argName("FILE").longOpt(OPTION_FILEPATH)
				.desc("The path to the license ZIP file which should be used.").build();
		Option instrumentation = Option.builder(OPTION_INSTRUMENTATION_SHORT).required(false).argName("INSTRUMENTATION")
				.longOpt(OPTION_INSTRUMENTATION).desc("Specifies if the PROBE should be started using instrumenation").build();

		options.addOption(filePath);
		options.addOption(instrumentation);
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(options, args);
			ProbeCLI client = new ProbeCLI();

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
				client.startWorkerThreads();
			}

		} catch (ParseException e) {
			String header = "Start monitoring your system using Probe\n\n";
			String footer = "\nPlease report issues at https://github.com/secinto/simple2secure/issues";
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ProbeCLI", header, options, footer, true);
		}

	}
}
