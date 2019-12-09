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
package com.simple2secure.probe.config;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.simple2secure.api.model.OsQuery;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.Step;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.commons.rest.RESTUtils;
import com.simple2secure.probe.license.LicenseController;
import com.simple2secure.probe.network.PacketProcessor;
import com.simple2secure.probe.network.PacketProcessorFSM;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.JsonUtils;
import com.simple2secure.probe.utils.ProbeUtils;

public class ProbeConfiguration {

	private static Logger log = LoggerFactory.getLogger(ProbeConfiguration.class);

	private static ProbeConfiguration instance;

	private static boolean apiAvailable = false;
	public static boolean runInTesting = true;

	public static String authKey = "";
	public static String groupId = "";
	public static String probeId = "";
	public static String hostname = "";
	public static String netmask = "";
	public static String ipAddress = "";
	public static String licenseId = "";
	public static String osinfo = "";
	public static String licensePath = "";
	public static String osQueryExecutablePath = "";
	public static String osQueryConfigPath = "";

	public static boolean isLicenseValid = false;
	public static boolean isCheckingLicense = false;
	public static boolean isGuiRunning = false;

	private Map<String, Step> currentSteps;

	private Map<String, PacketProcessor> currentPacketProcessors;

	private Map<String, Processor> currentProcessors;

	private Map<String, OsQuery> currentQueries;

	private LicenseController licenseController = new LicenseController();

	private static PropertyChangeSupport support;

	public static ResourceBundle rb;

	/***
	 * Returns the configuration if already initialized. If not, it tries retrieving it from the standard path, the database, and the WebAPI
	 * by calling updateConfig()
	 *
	 * @return Initialized ApplicationConfiguration object
	 * @throws IllegalArgumentException
	 */
	public static ProbeConfiguration getInstance() throws IllegalArgumentException {
		if (instance == null) {
			instance = new ProbeConfiguration();
		}
		return instance;
	}

	/**
	 * General constructor which creates the config DAO.
	 */
	private ProbeConfiguration() {
		support = new PropertyChangeSupport(this);
		currentPacketProcessors = new HashMap<>();
		currentSteps = new HashMap<>();
		currentProcessors = new HashMap<>();
		currentQueries = new HashMap<>();
		rb = ResourceBundle.getBundle("messageCodes", new java.util.Locale("en"));
		osinfo = ProbeUtils.getOsinfo();

		if (isAPIAvailable()) {
			checkAndUpdateConfigFromAPI();
		} else {
			loadConfig();
		}

	}

	/**
	 * Adds a listener for changing properties. It gets informed if internal state variables are changing.
	 *
	 * @param pcl
	 *          The listener to add.
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	/**
	 * Removes the specified listener from being informed about changing properties.
	 *
	 * @param pcl
	 *          The listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	/***
	 * This method tries to acquire the newest Configuration file with all means necessary
	 *
	 * Detailed process: It checks the Database for Configuration files and reads in the newest. With the URL provided in the Configuration
	 * file from the Database, this method contacts the WebAPI and checks whether a newer version is available. If it is so, it will acquire
	 * it from there. If none are found, the offline Configuration provided will be used
	 *
	 * @param standardConfig
	 *          the path to the offline Configuration file. <code>StaticConfigValues.XML_LOCATION</code> can be used.
	 */
	private void loadConfig() {
		updateProcessorsLocal();
		updateStepsLocal();
		updateQueriesLocal();
	}

	/**
	 * Obtains the configuration from the Portal and updates the local configuration accordingly. The retrieved information is stored in the
	 * database and the packet processors are instantiated.
	 */
	public void checkAndUpdateConfigFromAPI() {
		/*
		 * Check if the API is available and if a newer version is available update it.
		 */
		if (isAPIAvailable()) {
			/*
			 * Verifies if the token is still valid or updates it if necessary.
			 */
			verifyLicense();

			if (isLicenseValid) {

				updateProcessorsFromAPI();
				updateStepsFromAPI();
				updateQueriesFromAPI();

			}

		}
	}

	/**
	 * Verifies if the currently used license and token is still valid. The license is updated if something has changed in the Portal. If the
	 * license is not valid anymore the properties are set accordingly.
	 */
	private void verifyLicense() {
		if (licenseController.authenticateLicense()) {
			isLicenseValid = true;
			if (support != null) {
				support.firePropertyChange("isLicenseValid", ProbeConfiguration.isLicenseValid, isLicenseValid);
			}
		} else {
			isLicenseValid = false;
			if (support != null) {
				support.firePropertyChange("isLicenseValid", ProbeConfiguration.isLicenseValid, isLicenseValid);
			}
		}
	}

	/**
	 * Updates the {@link Processor} objects, which are used to perform actions for network monitoring, from the Portal. Updates the local
	 * configuration accordingly.
	 */
	private void updateProcessorsFromAPI() {
		List<Processor> apiProcessors = getProcessorsFromAPI();

		if (apiProcessors != null) {
			/*
			 * Use the configuration from the API and clear existing entries.
			 */
			DBUtil.getInstance().clearDB(Processor.class);

			for (Processor processor : apiProcessors) {
				DBUtil.getInstance().merge(processor);
			}
			/*
			 * Obtain the processors stored in the DB to update the currentProcessors.
			 */
			List<Processor> dbProcessors = getProcessorsFromDatabase();
			currentProcessors.clear();
			for (Processor processor : dbProcessors) {
				currentProcessors.put(processor.getName(), processor);
			}
			log.info("Using processors configuration from server!");
		}
	}

	/**
	 * Updates the current processors from local information, either the database if available or from the default file configuration
	 * otherwise.
	 */
	private void updateProcessorsLocal() {
		List<Processor> dbProcessors = getProcessorsFromDatabase();
		/*
		 * Obtain initial configuration from file and store it to the DB if none is available yet.
		 */
		if (dbProcessors == null || dbProcessors.size() == 0) {
			log.debug("DB processors not available, reading from file. Should only happen once.");
			List<Processor> fileProcessors = getProcessorsFromFile();
			for (Processor processor : fileProcessors) {
				DBUtil.getInstance().merge(processor);
			}
			/*
			 * Obtain the processors stored in the DB to also obtain the ID.
			 */
			dbProcessors = getProcessorsFromDatabase();
		}

		for (Processor processor : dbProcessors) {
			currentProcessors.put(processor.getName(), processor);
		}

	}

	/**
	 * Updates the {@link Step} objects, which are used to define which steps (processors) are actually used to monitor the network traffic,
	 * from the API. Updates the local configuration accordingly.
	 */
	private void updateStepsFromAPI() {
		List<Step> apiSteps = getStepsFromAPI();
		if (apiSteps != null) {

			DBUtil.getInstance().clearDB(Step.class);

			for (Step step : apiSteps) {
				DBUtil.getInstance().merge(step);
			}
			/*
			 * Obtain the steps stored in the DB to update the currentSteps.
			 */
			List<Step> dbSteps = getStepsFromDatabase();
			currentSteps.clear();
			for (Step step : dbSteps) {
				currentSteps.put(step.getName(), step);
			}

			/*
			 * Perform an update of the packet processors since they may have changed.
			 */
			updatePacketProcessors();
			log.info("Using steps configuration from server!");

		}
	}

	/**
	 * Updates the current steps from local information, either the database if available or from the default file configuration otherwise.
	 */
	private void updateStepsLocal() {
		List<Step> dbSteps = getStepsFromDatabase();

		/*
		 * Obtain initial configuration from file and store it to the DB if none is available yet.
		 */
		if (dbSteps == null || dbSteps.size() == 0) {
			log.debug("DB steps not available, reading from file. Should only happen once.");
			List<Step> fileSteps = getStepsFromFile();
			for (Step step : fileSteps) {
				step.setActive(1);
				DBUtil.getInstance().merge(step);
			}
			/*
			 * Obtain the processors stored in the DB to also obtain the ID.
			 */
			dbSteps = getStepsFromDatabase();
		}

		for (Step step : dbSteps) {
			currentSteps.put(step.getName(), step);
		}

	}

	/**
	 * Updates the {@link OsQuery} objects, which are used to obtain system information using OSQuery, from the API. Updates the local
	 * configuration accordingly.
	 */
	private void updateQueriesFromAPI() {
		List<OsQuery> apiQueries = getQueriesFromAPI();
		if (apiQueries != null) {
			DBUtil.getInstance().clearDB(OsQuery.class);

			for (OsQuery query : apiQueries) {
				if (query != null) {
					DBUtil.getInstance().merge(query);
				}
			}
			/*
			 * Obtain the processors stored in the DB to also obtain the ID.
			 */
			List<OsQuery> dbQueries = getQueriesFromDatabase();
			currentQueries.clear();
			for (OsQuery query : dbQueries) {
				currentQueries.put(query.getName(), query);
			}
			log.info("Using queries configuration from server!");

		}
	}

	/**
	 * Updates the current queries from local information, either the database if available or from the default file configuration otherwise.
	 */
	private void updateQueriesLocal() {
		/*
		 * Obtain currently stored configurations from database.
		 */
		List<OsQuery> dbQueries = getQueriesFromDatabase();

		if (dbQueries == null || dbQueries.size() == 0) {
			log.debug("DB steps not available, reading from file. Should only happen once.");
			List<OsQuery> fileQueries = getQueriesFromFile();
			for (OsQuery query : fileQueries) {
				DBUtil.getInstance().merge(query);
			}
			dbQueries = getQueriesFromDatabase();
		}

		for (OsQuery query : dbQueries) {
			currentQueries.put(query.getName(), query);
		}

	}

	/**
	 * Updates the packet processors locally, which are used from the {@link PacketProcessorFSM}.
	 */
	private void updatePacketProcessors() {
		/*
		 * Instantiate the actual packet processors currently defined in the database.
		 */
		Map<String, PacketProcessor> updatedPacketProcessors = new HashMap<>();
		for (Step step : currentSteps.values()) {
			try {
				if (currentPacketProcessors.containsKey(step.getName())) {
					updatedPacketProcessors.put(step.getName(), currentPacketProcessors.get(step.getName()));
				} else {
					Processor processor = currentProcessors.get(step.getName());
					if (processor != null) {
						Class<?> processorClass = Class.forName(processor.getProcessor_class());
						Constructor<?> constructor = processorClass.getConstructor(String.class, Map.class);
						Map<String, String> options = new HashMap<>();
						PacketProcessor packetProcessor = (PacketProcessor) constructor.newInstance(step.getName(), options);
						updatedPacketProcessors.put(processor.getName(), packetProcessor);
					}
				}
			} catch (Exception e) {
				log.error("Couldn't create packet processor for configuration entry {} with. Reason {}", step.getName(), e);
			}
		}
		currentPacketProcessors = updatedPacketProcessors;

	}

	/**
	 * Returns a List of {@link Processor} objects from the associated Portal API.
	 *
	 * @return The obtained List of {@link Processor} objects.
	 */
	public List<Processor> getProcessorsFromAPI() {
		String response = RESTUtils.sendGet(LoadedConfigItems.getInstance().getProcessorAPI(), ProbeConfiguration.authKey);
		if (!Strings.isNullOrEmpty(response)) {
			Processor[] processorArray = JSONUtils.fromString(response, Processor[].class);
			if (processorArray != null && processorArray.length > 0) {
				return Arrays.asList(processorArray);
			} else {
				log.trace("No processors specified for PROBE");
			}
		} else {
			log.error("Getting specified list of processors for PROBE was not successful.");
		}
		return null;
	}

	/**
	 * This function reads processors from database
	 *
	 * @return
	 */
	private List<Processor> getProcessorsFromDatabase() {
		List<Processor> processors = DBUtil.getInstance().findAll(Processor.class);
		return processors;
	}

	/**
	 * This function reads RunQueries from file
	 *
	 * @return
	 * @throws IOException
	 */
	private List<Processor> getProcessorsFromFile() {
		File potentialProcessors;
		List<Processor> processors = new ArrayList<>();

		String processorsContent = null;
		try {

			potentialProcessors = new File(StaticConfigItems.PROCESSORS_JSON_LOCATION);

			if (!potentialProcessors.exists()) {
				processorsContent = IOUtils.toString(getClass().getResourceAsStream(StaticConfigItems.PROCESSORS_JSON_LOCATION), "UTF-8");
			} else {
				processorsContent = Files.toString(potentialProcessors, Charsets.UTF_8);
			}
		} catch (IOException e) {
			log.error("Provided resource location {} is not correct!", StaticConfigItems.PROCESSORS_JSON_LOCATION);
		}

		processors = JsonUtils.readProcessorsFromString(processorsContent);

		return processors;
	}

	/**
	 * Returns a List of {@link Step} objects from the associated Portal API.
	 *
	 * @return The obtained List of {@link Step} objects.
	 */
	private List<Step> getStepsFromAPI() {
		String response = RESTUtils.sendGet(LoadedConfigItems.getInstance().getStepAPI() + "?select_all=false", ProbeConfiguration.authKey);

		if (!Strings.isNullOrEmpty(response)) {
			Step[] stepArray = JSONUtils.fromString(response, Step[].class);
			if (stepArray != null && stepArray.length > 0) {
				return Arrays.asList(stepArray);
			} else {
				log.trace("No steps specified for PROBE");
			}
		} else {
			log.error("Getting specified list of steps for PROBE was not successful.");
		}
		return null;
	}

	/**
	 * This function reads steps from database
	 *
	 * @return
	 */
	private List<Step> getStepsFromDatabase() {
		List<Step> dbSteps = DBUtil.getInstance().findByFieldName("active", 1, Step.class);
		return dbSteps;
	}

	/**
	 * This function reads steps from file
	 *
	 * @return
	 */
	private List<Step> getStepsFromFile() {
		File potentialSteps;
		List<Step> steps = new ArrayList<>();

		String stepsContent = null;
		try {

			potentialSteps = new File(StaticConfigItems.STEPS_JSON_LOCATION);

			if (!potentialSteps.exists()) {
				stepsContent = IOUtils.toString(getClass().getResourceAsStream(StaticConfigItems.STEPS_JSON_LOCATION), "UTF-8");
			} else {
				stepsContent = Files.toString(potentialSteps, Charsets.UTF_8);
			}
		} catch (IOException e) {
			log.error("Provided resource location {} is not correct!", StaticConfigItems.STEPS_JSON_LOCATION);
		}

		steps = JsonUtils.readStepsFromString(stepsContent);

		return steps;

	}

	/**
	 * Returns a List of {@link OsQuery} objects from the associated Portal API.
	 *
	 * @return The obtained List of {@link OsQuery} objects.
	 */
	private List<OsQuery> getQueriesFromAPI() {
		String response = RESTUtils.sendGet(LoadedConfigItems.getInstance().getQueryAPI() + "/" + ProbeConfiguration.probeId + "/"
				+ ProbeConfiguration.osinfo + "?select_all=false", ProbeConfiguration.authKey);
		if (!Strings.isNullOrEmpty(response)) {
			OsQuery[] queryRunArray = JSONUtils.fromString(response, OsQuery[].class);
			if (queryRunArray != null && queryRunArray.length > 0) {
				return Arrays.asList(queryRunArray);
			} else {
				log.trace("No queries specified for PROBE");
			}
		} else {
			log.error("Getting specified list of queries for PROBE was not successful.");
		}
		return null;
	}

	/**
	 * This function reads runQueries from database
	 *
	 * @return
	 */
	private List<OsQuery> getQueriesFromDatabase() {
		return DBUtil.getInstance().findByFieldName("active", 1, new com.simple2secure.api.model.OsQuery());
	}

	/**
	 * This function reads RunQueries from file
	 *
	 * @return
	 */
	private List<OsQuery> getQueriesFromFile() {
		File potentialQueries;
		List<OsQuery> queries = new ArrayList<>();

		String queriesContent = null;
		try {

			potentialQueries = new File(StaticConfigItems.QUERIES_JSON_LOCATION);

			if (!potentialQueries.exists()) {
				queriesContent = IOUtils.toString(getClass().getResourceAsStream(StaticConfigItems.QUERIES_JSON_LOCATION), "UTF-8");
			} else {
				queriesContent = Files.toString(potentialQueries, Charsets.UTF_8);
			}
		} catch (IOException e) {
			log.error("Provided resource location {} is not correct!", StaticConfigItems.QUERIES_JSON_LOCATION);
		}

		queries = JsonUtils.readRunQueriesFromString(queriesContent);

		return queries;
	}

	/**
	 * Returns whether the Portal API is available or not currently.
	 *
	 * @return True if the API is available
	 */
	public static boolean isAPIAvailable() {
		return apiAvailable;
	}

	/**
	 * Sets the API availability to the specified value.
	 *
	 * @param apiAvailability
	 *          The availability of the API (true or false)
	 */
	public static void setAPIAvailablitity(boolean apiAvailability) {
		apiAvailable = apiAvailability;
		if (support != null) {
			support.firePropertyChange("isApiAvailable", apiAvailable, apiAvailability);
		}
	}

	/**
	 * Returns a map containing the current steps which are defined for the Probe. These Steps are used in the {@link PacketProcessorFSM} to
	 * obtain from what Processor the currently received packet needs to be processed.
	 *
	 * @return
	 */
	public Map<String, Step> getCurrentSteps() {
		return currentSteps;
	}

	/**
	 * Returns the map of currently specified {@link PacketProcessor} objects.
	 *
	 * @return The map of {@link PacketProcessor} objects.
	 */
	public Map<String, PacketProcessor> getCurrentPacketProcessors() {
		return currentPacketProcessors;
	}

	/**
	 * Returns a map of currently specified {@link Processor} objects.
	 *
	 * @return The map of {@link Processor} objects
	 */
	public Map<String, Processor> getCurrentProcessors() {
		return currentProcessors;
	}

	/**
	 * Returns a map of currently specified {@link OsQuery} objects.
	 *
	 * @return
	 */
	public Map<String, OsQuery> getCurrentQueries() {
		return currentQueries;
	}
}
