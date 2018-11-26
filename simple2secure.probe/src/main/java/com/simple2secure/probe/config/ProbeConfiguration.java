package com.simple2secure.probe.config;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.Config;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.QueryRun;
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
import com.simple2secure.probe.utils.LocaleHolder;

public class ProbeConfiguration {

	private static Logger log = LoggerFactory.getLogger(ProbeConfiguration.class);

	private static ProbeConfiguration instance;

	private static boolean apiAvailable = false;

	public static String authKey = "";

	public static String probeId = "";

	public static String licenseId = "";

	public static boolean isLicenseValid = false;
	public static boolean isCheckingLicense = false;
	public static boolean isGuiRunning = false;

	private Config currentConfig;

	private Map<String, Step> currentSteps;

	private Map<String, PacketProcessor> currentPacketProcessors;

	private Map<String, Processor> currentProcessors;

	private Map<String, QueryRun> currentQueries;

	private LicenseController licenseController = new LicenseController();

	private static PropertyChangeSupport support;

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
		currentPacketProcessors = new HashMap<>();
		currentSteps = new HashMap<>();
		currentProcessors = new HashMap<>();
		currentQueries = new HashMap<>();
		loadConfig();
		support = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	public Config getConfig() {
		if (currentConfig != null) {
			return currentConfig;
		} else {
			return loadConfig();
		}
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
	public Config loadConfig() {
		updateConfigLocal();
		updateProcessorsLocal();
		updateStepsLocal();
		updateQueriesLocal();

		checkConfig();

		return currentConfig;
	}

	/**
	 * Obtains the configuration from the server and updates the local configuration accordingly. The retrieved information is stored in the
	 * database and the packet processors are instantiated.
	 */
	public void checkConfig() {
		/*
		 * Check if the API is available and if a newer version is available update it.
		 */
		if (isAPIAvailable()) {
			/*
			 * Verifies if the token is still valid or updates it if necessary.
			 */
			verifyLicense();

			if (isLicenseValid) {

				updateConfigFromAPI();
				updateProcessorsFromAPI();
				updateStepsFromAPI();
				updateQueriesFromAPI();

			}

		}
	}

	private void verifyLicense() {
		CompanyLicensePublic licenseObj = licenseController.checkTokenValidity();

		if (licenseObj != null) {
			DBUtil.getInstance().merge(licenseObj);
			authKey = licenseObj.getAccessToken();
			isLicenseValid = true;
			support.firePropertyChange("isLicenseValid", ProbeConfiguration.isLicenseValid, isLicenseValid);
		} else {
			CompanyLicensePublic license = licenseController.loadLicenseFromDB();
			DBUtil.getInstance().delete(license);
			isLicenseValid = false;
			support.firePropertyChange("isLicenseValid", ProbeConfiguration.isLicenseValid, isLicenseValid);
		}
	}

	private void updateConfigFromAPI() {
		Config apiConfig = getConfigFromAPI();

		if (apiConfig != null && apiConfig.getVersion() >= currentConfig.getVersion()) {
			apiConfig.setId(currentConfig.getId());
			DBUtil.getInstance().merge(apiConfig);
			/*
			 * Obtain it from the database to have all merged fields correctly updated.
			 *
			 * TODO: Although there will probably be no changes. Verify if this is necessary
			 */
			currentConfig = getConfigFromAPI();
			log.info("Using configuration from the server!");
		}
	}

	/**
	 * Updates the current configuration from local information, either the database if available or from the default file configuration
	 * otherwise.
	 */
	private void updateConfigLocal() {
		Config dbConfig = getConfigFromDatabase();
		/*
		 * Obtain initial configuration from file and store it to the DB if none is available yet.
		 */
		if (dbConfig == null) {
			log.debug("DB config not available, reading config from file. Should only happen once.");
			Config fileConfig = getConfigFromFile();
			DBUtil.getInstance().save(fileConfig);
			/*
			 * Obtain the config stored in the DB to also obtain the ID.
			 *
			 * TODO: Currently the case that it is still not stored is not handled.
			 */
			currentConfig = getConfigFromDatabase();
		} else {
			currentConfig = dbConfig;
		}
	}

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

	private void updateQueriesFromAPI() {
		List<QueryRun> apiQueries = getQueriesFromAPI();
		if (apiQueries != null) {
			DBUtil.getInstance().clearDB(QueryRun.class);

			for (QueryRun query : apiQueries) {
				if (query != null) {
					DBUtil.getInstance().merge(query);
				}
			}
			/*
			 * Obtain the processors stored in the DB to also obtain the ID.
			 */
			List<QueryRun> dbQueries = getQueriesFromDatabase();
			currentQueries.clear();
			for (QueryRun query : dbQueries) {
				currentQueries.put(query.getName(), query);
			}
		}
	}

	/**
	 * Updates the current queries from local information, either the database if available or from the default file configuration otherwise.
	 */
	private void updateQueriesLocal() {
		/*
		 * Obtain currently stored configurations from database.
		 */
		List<QueryRun> dbQueries = getQueriesFromDatabase();

		if (dbQueries == null || dbQueries.size() == 0) {
			log.debug("DB steps not available, reading from file. Should only happen once.");
			List<QueryRun> fileQueries = getQueriesFromFile();
			for (QueryRun query : fileQueries) {
				query.setActive(1);
				DBUtil.getInstance().merge(query);
			}
			dbQueries = getQueriesFromDatabase();
		}

		for (QueryRun query : dbQueries) {
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
	 *
	 * @return
	 */
	public Config getConfigFromAPI() {
		return JSONUtils.fromString(RESTUtils.sendGet(LoadedConfigItems.getInstance().getConfigAPI(), ProbeConfiguration.authKey),
				Config.class);
	}

	/**
	 *
	 * @return
	 */
	public Config getConfigFromDatabase() {
		List<Config> configs = DBUtil.getInstance().findAll(new Config());
		if (configs != null && configs.size() == 1) {
			return configs.get(0);
		}
		return null;
	}

	/**
	 *
	 * @return
	 */
	public Config getConfigFromFile() {
		Config fileConfig = null;
		try {
			InputStream ispotentialConfig = ProbeConfiguration.class.getResourceAsStream(StaticConfigItems.CONFIG_JSON_LOCATION);
			StringWriter writer = new StringWriter();
			IOUtils.copy(ispotentialConfig, writer, "UTF-8");
			String potentialConfigString = writer.toString();
			File potentialConfig = new File(potentialConfigString);

			if (!Strings.isNullOrEmpty(potentialConfigString)) {
				fileConfig = JsonUtils.readConfigFromString(potentialConfigString);
				if (fileConfig == null) {
					log.error("Couldn't obtain configuration object from the file content of configuration file {}",
							potentialConfig.getAbsoluteFile());
					throw new IllegalArgumentException(LocaleHolder.getMessage("load_config_failed").getMessage());
				}
			} else {
				log.error("Couldn't obtain the standard configuration file!");
				System.out.println(LocaleHolder.getMessage("load_config_failed").getMessage());
				throw new IllegalArgumentException(LocaleHolder.getMessage("load_config_failed").getMessage());
			}

		} catch (IOException e) {
			log.error("Could't open config file {} and load the configuration", StaticConfigItems.CONFIG_JSON_LOCATION);
			System.out.println(LocaleHolder.getMessage("load_config_failed").getMessage());
			throw new IllegalArgumentException(LocaleHolder.getMessage("load_config_failed").getMessage());
		}
		return fileConfig;
	}

	/**
	 * This function returns processors from the API
	 *
	 * @return
	 */
	public List<Processor> getProcessorsFromAPI() {
		return Arrays.asList(JSONUtils.fromString(
				RESTUtils.sendGet(LoadedConfigItems.getInstance().getProcessorAPI() + "/" + ProbeConfiguration.probeId, ProbeConfiguration.authKey),
				Processor[].class));
	}

	/**
	 * This function reads processors from database
	 *
	 * @return
	 */
	private List<Processor> getProcessorsFromDatabase() {
		List<Processor> processors = DBUtil.getInstance().findByFieldName("active", 1, new Processor());
		return processors;
	}

	/**
	 * This function reads RunQueries from file
	 *
	 * @return
	 */
	private List<Processor> getProcessorsFromFile() {
		File potentialProcessors;
		List<Processor> processors = new ArrayList<>();

		potentialProcessors = new File(StaticConfigItems.PROCESSORS_JSON_LOCATION);

		if (!potentialProcessors.exists()) {
			try {
				potentialProcessors = new File(ProbeConfiguration.class.getResource(StaticConfigItems.PROCESSORS_JSON_LOCATION).toURI());
			} catch (URISyntaxException e) {
				log.error("Provided file URI is not correct!" + potentialProcessors.getAbsolutePath());
			}
			if (!potentialProcessors.exists()) {
				log.error("The specified file couldn't be found! {}", StaticConfigItems.QUERIES_JSON_LOCATION);
				throw new IllegalArgumentException("The specified file couldn't be found!");
			}
		}

		processors = JsonUtils.readProcessorsFromFile(potentialProcessors);

		return processors;
	}

	/**
	 * This function returns steps from the API for the logged in user
	 */
	private List<Step> getStepsFromAPI() {
		return Arrays.asList(
				JSONUtils.fromString(RESTUtils.sendGet(LoadedConfigItems.getInstance().getStepAPI() + "/" + ProbeConfiguration.probeId + "/false",
						ProbeConfiguration.authKey), Step[].class));
	}

	/**
	 * This function reads steps from database
	 *
	 * @return
	 */
	private List<Step> getStepsFromDatabase() {
		List<Step> dbSteps = DBUtil.getInstance().findByFieldName("active", 1, new Step());
		return dbSteps;
	}

	/**
	 * This function reads steps from file
	 *
	 * @return
	 */
	private static List<Step> getStepsFromFile() {
		File potentialSteps;
		List<Step> fileSteps = new ArrayList<>();

		potentialSteps = new File(StaticConfigItems.STEPS_JSON_LOCATION);

		if (!potentialSteps.exists()) {
			try {
				potentialSteps = new File(ProbeConfiguration.class.getResource(StaticConfigItems.STEPS_JSON_LOCATION).toURI());
			} catch (URISyntaxException e) {
				log.error("Provided file URI is not correct!" + potentialSteps.getAbsolutePath());
			}
			if (!potentialSteps.exists()) {
				log.error("The specified file couldn't be found! {}" + StaticConfigItems.STEPS_JSON_LOCATION);
				throw new IllegalArgumentException("The specified file couldn't be found!");
			}
		}

		fileSteps = JsonUtils.readStepsFromFile(potentialSteps);

		return fileSteps;
	}

	/**
	 * This function retrieves the run Queries from API. Currently we are getting queries only for current user, but we will also have to add
	 * additional parameter for client.
	 *
	 * @return
	 */
	private List<QueryRun> getQueriesFromAPI() {
		return Arrays.asList(JSONUtils
				.fromString(RESTUtils.sendGet(LoadedConfigItems.getInstance().getQueryAPI() + "/" + ProbeConfiguration.probeId + "/" + false,
						ProbeConfiguration.authKey), QueryRun[].class));
	}

	/**
	 * This function reads runQueries from database
	 *
	 * @return
	 */
	private static List<QueryRun> getQueriesFromDatabase() {
		return DBUtil.getInstance().findByFieldName("active", 1, new com.simple2secure.api.model.QueryRun());
	}

	/**
	 * This function reads RunQueries from file
	 *
	 * @return
	 */
	private static List<QueryRun> getQueriesFromFile() {
		File potentialRunQueries;
		List<QueryRun> runQueries = new ArrayList<>();

		potentialRunQueries = new File(StaticConfigItems.QUERIES_JSON_LOCATION);

		if (!potentialRunQueries.exists()) {
			try {
				potentialRunQueries = new File(ProbeConfiguration.class.getResource(StaticConfigItems.QUERIES_JSON_LOCATION).toURI());
			} catch (URISyntaxException e) {
				log.error("Provided file URI is not correct!" + potentialRunQueries.getAbsolutePath());
			}
			if (!potentialRunQueries.exists()) {
				log.error("The specified file couldn't be found! {}" + StaticConfigItems.QUERIES_JSON_LOCATION);
				throw new IllegalArgumentException("The specified file couldn't be found!");
			}
		}

		runQueries = JsonUtils.readRunQueriesFromFile(potentialRunQueries);

		return runQueries;
	}

	public Config getCurrentConfigObj() {
		return currentConfig;
	}

	public static boolean isAPIAvailable() {
		return apiAvailable;
	}

	public static void setAPIAvailablitity(boolean apiAvailability) {
		apiAvailable = apiAvailability;
		if (support != null) {
			support.firePropertyChange("isApiAvailable", apiAvailable, apiAvailability);
		}
	}

	public Map<String, Step> getCurrentSteps() {
		return currentSteps;
	}

	public Map<String, PacketProcessor> getCurrentPacketProcessors() {
		return currentPacketProcessors;
	}

	public Map<String, Processor> getCurrentProcessors() {
		return currentProcessors;
	}

	public Map<String, QueryRun> getCurrentQueries() {
		return currentQueries;
	}
}
