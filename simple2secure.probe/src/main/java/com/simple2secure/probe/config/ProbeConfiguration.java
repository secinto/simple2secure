package com.simple2secure.probe.config;

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
import com.google.gson.Gson;
import com.simple2secure.api.config.ConfigItems;
import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.api.model.Config;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.api.model.Step;
import com.simple2secure.probe.gui.ProbeGUI;
import com.simple2secure.probe.network.PacketProcessor;
import com.simple2secure.probe.utils.APIUtils;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.JsonUtils;
import com.simple2secure.probe.utils.LocaleHolder;

public class ProbeConfiguration {

	private static Logger log = LoggerFactory.getLogger(ProbeConfiguration.class);

	private static ProbeConfiguration instance;

	private static Gson gson = new Gson();

	private static boolean apiAvailable = false;

	public static String authKey = "";

	public static String probeId = "";

	public static String licenseId = "";

	public static boolean isCheckingLicense = false;

	public static boolean isLicenseValid = false;

	private Config currentConfig;

	private Map<String, Step> currentSteps;

	private Map<String, PacketProcessor> currentPacketProcessors;

	private Map<String, Processor> currentProcessors;

	private Map<String, QueryRun> currentQueries;

	/***
	 * Returns the configuration if already initialized. If not, it tries retrieving
	 * it from the standard path, the database, and the WebAPI by calling
	 * updateConfig()
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
		currentPacketProcessors = new HashMap<String, PacketProcessor>();
		currentSteps = new HashMap<String, Step>();
		currentProcessors = new HashMap<String, Processor>();
		currentQueries = new HashMap<String, QueryRun>();
		getConfig();
	}

	/***
	 * This method tries to acquire the newest Configuration file with all means
	 * necessary
	 *
	 * Detailed process: It checks the Database for Configuration files and reads in
	 * the newest. With the URL provided in the Configuration file from the
	 * Database, this method contacts the WebAPI and checks whether a newer version
	 * is available. If it is so, it will acquire it from there. If none are found,
	 * the offline Configuration provided will be used
	 *
	 * @param standardConfig
	 *            the path to the offline Configuration file.
	 *            <code>StaticConfigValues.XML_LOCATION</code> can be used.
	 */
	public void getConfig() {

		/*
		 * Obtain currently stored configurations from database.
		 */
		Config dbConfig = getConfigFromDatabase();
		List<Processor> dbProcessors = getProcessorsFromDatabase();
		List<Step> dbSteps = getStepsFromDatabase();
		List<QueryRun> dbQueries = getQueriesFromDatabase();

		/*
		 * Obtain initial configuration from file and store it to the DB if none is
		 * available yet.
		 */
		if (dbConfig == null) {
			log.debug("DB config not available, reading config from file. Should only happen once.");
			Config fileConfig = getConfigFromFile();
			fileConfig.setProbeId(ProbeConfiguration.probeId);
			DBUtil.getInstance().save(fileConfig);
			/*
			 * Obtain the config stored in the DB to also obtain the ID.
			 */
			currentConfig = getConfigFromDatabase();
		} else {
			currentConfig = dbConfig;
		}
		/*
		 * Obtain initial configuration from file and store it to the DB if none is
		 * available yet.
		 */
		if (dbProcessors == null || dbProcessors.size() == 0) {
			log.debug("DB processors not available, reading from file. Should only happen once.");
			List<Processor> fileProcessors = getProcessorsFromFile();
			for (Processor processor : fileProcessors) {
				processor.setProbeId(ProbeConfiguration.probeId);
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

		/*
		 * Obtain initial configuration from file and store it to the DB if none is
		 * available yet.
		 */
		if (dbSteps == null || dbSteps.size() == 0) {
			log.debug("DB steps not available, reading from file. Should only happen once.");
			List<Step> fileSteps = getStepsFromFile();
			for (Step step : fileSteps) {
				step.setProbeId(ProbeConfiguration.probeId);
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

		if (dbQueries == null || dbQueries.size() == 0) {
			log.debug("DB steps not available, reading from file. Should only happen once.");
			List<QueryRun> fileQueries = getQueriesFromFile();
			for (QueryRun query : fileQueries) {
				query.setProbeId(ProbeConfiguration.probeId);
				query.setActive(1);
				DBUtil.getInstance().merge(query);
			}
			dbQueries = getQueriesFromDatabase();
		}

		for (QueryRun query : dbQueries) {
			currentQueries.put(query.getName(), query);
		}

		/*
		 * Check if something new is available from the API
		 */
		checkConfig();
		if (isLicenseValid) {
			updatePacketProcessors();
		}

	}

	/**
	 * Obtains the configuration from the server and updates the local configuration
	 * accordingly. The retrieved information is stored in the database and the
	 * packet processors are instantiated.
	 */
	public void checkConfig() {
		/*
		 * Check if the API is available and if a newer version is available update it.
		 */
		if (isAPIAvailable()) {
			CompanyLicenseObj licenseObj = checkTokenValidity();

			if (licenseObj != null) {
				DBUtil.getInstance().merge(licenseObj);
				authKey = licenseObj.getAuthToken();
				isLicenseValid = true;
				isCheckingLicense = false;
			} else {
				/// Delete license object from the db and change to the license import view!
				CompanyLicenseObj license = ProbeGUI.getLicenseFromDb();
				DBUtil.getInstance().delete(license);
				isLicenseValid = false;
			}

			if (isLicenseValid) {
				Config apiConfig = getConfigFromAPI();
				List<Processor> apiProcessors = getProcessorsFromAPI();
				List<Step> apiSteps = getStepsFromAPI();
				List<QueryRun> apiQueries = getQueriesFromAPI();

				log.debug("Queries obtained from API {}", apiQueries.size());
				if (apiConfig != null && apiConfig.getVersion() >= currentConfig.getVersion()) {
					apiConfig.setId(currentConfig.getId());
					apiConfig.setProbeId(ProbeConfiguration.probeId);
					DBUtil.getInstance().merge(apiConfig);
					currentConfig = apiConfig;
					log.info("Using configuration from the server!");
				}

				if (apiProcessors != null) {

					DBUtil.getInstance().clearDB(Processor.class);

					for (Processor processor : apiProcessors) {
						processor.setProbeId(ProbeConfiguration.probeId);
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

				if (apiSteps != null) {

					DBUtil.getInstance().clearDB(Step.class);

					for (Step step : apiSteps) {
						step.setProbeId(ProbeConfiguration.probeId);
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

				if (apiQueries != null) {
					DBUtil.getInstance().clearDB(QueryRun.class);

					for (QueryRun query : apiQueries) {
						if (query != null) {
							query.setProbeId(ProbeConfiguration.probeId);
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

		}
	}

	private CompanyLicenseObj checkTokenValidity() {
		isCheckingLicense = true;
		CompanyLicenseObj license = ProbeGUI.getLicenseFromDb();
		if (license != null) {
			String response = APIUtils.sendPostWithResponse(ConfigItems.license_api + "/token", license);
			if (!Strings.isNullOrEmpty(response)) {
				return gson.fromJson(response, CompanyLicenseObj.class);
			} else {
				return null;
			}
		} else {
			/*
			 * TODO: Create handling if license is not stored in DB.
			 */
			log.error("Couldn't find license in DB. Need to do something here");
			return null;
		}
	}

	/**
	 *
	 */
	private void updatePacketProcessors() {
		/*
		 * Instantiate the actual packet processors currently defined in the database.
		 */
		Map<String, PacketProcessor> updatedPacketProcessors = new HashMap<String, PacketProcessor>();
		for (Step step : currentSteps.values()) {
			try {
				if (currentPacketProcessors.containsKey(step.getName())) {
					updatedPacketProcessors.put(step.getName(), currentPacketProcessors.get(step.getName()));
				} else {
					Processor processor = currentProcessors.get(step.getName());
					if (processor != null) {
						Class<?> processorClass = Class.forName(processor.getProcessor_class());
						Constructor<?> constructor = processorClass.getConstructor(String.class, Map.class);
						Map<String, String> options = new HashMap<String, String>();
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
		return gson.fromJson(APIUtils.sendGet(ConfigItems.config_api + "/" + ProbeConfiguration.probeId), Config.class);
	}

	/**
	 *
	 * @return
	 */
	public Config getConfigFromDatabase() {
		return DBUtil.getInstance().findByFieldNameObject("probeId", probeId, new Config());
	}

	/**
	 *
	 * @return
	 */
	public Config getConfigFromFile() {
		Config fileConfig = null;
		try {
			InputStream ispotentialConfig = ProbeConfiguration.class.getResourceAsStream(ConfigItems.CONFIG_JSON_LOCATION);
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
			log.error("Could't open config file {} and load the configuration", ConfigItems.CONFIG_JSON_LOCATION);
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
		return Arrays
				.asList(gson.fromJson(APIUtils.sendGet(ConfigItems.processor_api + "/" + ProbeConfiguration.probeId), Processor[].class));
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

		potentialProcessors = new File(ConfigItems.PROCESSORS_JSON_LOCATION);

		if (!potentialProcessors.exists()) {
			try {
				potentialProcessors = new File(ProbeConfiguration.class.getResource(ConfigItems.PROCESSORS_JSON_LOCATION).toURI());
			} catch (URISyntaxException e) {
				log.error("Provided file URI is not correct!" + potentialProcessors.getAbsolutePath());
			}
			if (!potentialProcessors.exists()) {
				log.error("The specified file couldn't be found! {}", ConfigItems.QUERIES_JSON_LOCATION);
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
		return Arrays
				.asList(gson.fromJson(APIUtils.sendGet(ConfigItems.step_api + "/" + ProbeConfiguration.probeId + "/false"), Step[].class));
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

		potentialSteps = new File(ConfigItems.STEPS_JSON_LOCATION);

		if (!potentialSteps.exists()) {
			try {
				potentialSteps = new File(ProbeConfiguration.class.getResource(ConfigItems.STEPS_JSON_LOCATION).toURI());
			} catch (URISyntaxException e) {
				log.error("Provided file URI is not correct!" + potentialSteps.getAbsolutePath());
			}
			if (!potentialSteps.exists()) {
				log.error("The specified file couldn't be found! {}" + ConfigItems.STEPS_JSON_LOCATION);
				throw new IllegalArgumentException("The specified file couldn't be found!");
			}
		}

		fileSteps = JsonUtils.readStepsFromFile(potentialSteps);

		return fileSteps;
	}

	/**
	 * This function retrieves the run Queries from API. Currently we are getting
	 * queries only for current user, but we will also have to add additional
	 * parameter for client.
	 *
	 * @return
	 */
	private static List<QueryRun> getQueriesFromAPI() {
		return Arrays.asList(gson.fromJson(APIUtils.sendGet(ConfigItems.system_query_api + "/" + ProbeConfiguration.probeId + "/" + false),
				QueryRun[].class));
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

		potentialRunQueries = new File(ConfigItems.QUERIES_JSON_LOCATION);

		if (!potentialRunQueries.exists()) {
			try {
				potentialRunQueries = new File(ProbeConfiguration.class.getResource(ConfigItems.QUERIES_JSON_LOCATION).toURI());
			} catch (URISyntaxException e) {
				log.error("Provided file URI is not correct!" + potentialRunQueries.getAbsolutePath());
			}
			if (!potentialRunQueries.exists()) {
				log.error("The specified file couldn't be found! {}" + ConfigItems.QUERIES_JSON_LOCATION);
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
