package com.simple2secure.portal.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Config;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.Step;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRegistration;
import com.simple2secure.api.model.UserRegistrationType;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.repository.UserRepository;

@Component
public class DataInitialization {

	private static Logger log = LoggerFactory.getLogger(DataInitialization.class);

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	protected LoadedConfigItems loadedConfigItems;

	@Autowired
	protected SettingsRepository settingsRepository;

	@Autowired
	protected ProcessorRepository processorRepository;

	@Autowired
	protected GroupRepository groupRepository;

	@Autowired
	protected ConfigRepository configRepository;

	@Autowired
	protected QueryRepository queryRepository;

	@Autowired
	protected StepRepository stepRepository;

	@Autowired
	protected LicensePlanRepository licensePlanRepository;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected PortalUtils portalUtils;

	@Autowired
	protected UserUtils userUtils;

	private Gson gson = new Gson();

	/**
	 *
	 * @param userId
	 * @param username
	 *
	 *          This function adds a default group for the users which are registered using the standard registration. This function does not
	 *          apply when another user(superadmin, admin, superuser) adds new user, because he has to choose the group before adding.
	 * @throws IOException
	 */
	public void addDefaultGroup(String userId, String contextId) throws IOException {

		List<CompanyGroup> groupList = groupRepository.findByContextId(contextId);

		if (groupList == null || groupList.isEmpty()) {
			File file = new File(getClass().getResource("/server/group.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			CompanyGroup group = gson.fromJson(content, CompanyGroup.class);
			group.setContextId(contextId);
			group.setRootGroup(true);
			group.setStandardGroup(true);
			ObjectId groupId = groupRepository.saveAndReturnId(group);
			log.debug("Default group added for user with id {}", userId);
			if (!Strings.isNullOrEmpty(groupId.toString())) {
				try {
					addDefaultGroupQueries(groupId.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				addDefaultGroupProcessors(groupId.toString());
				addDefaultGroupSteps(groupId.toString());
			}
		}
	}

	/**
	 * This function adds default configuration for each group which is created
	 *
	 * @param probeId
	 * @throws IOException
	 */
	public void addDefaultConfiguration() throws IOException {
		List<Config> configDB = configRepository.findAll();
		if (configDB == null || configDB.isEmpty()) {
			File file = new File(getClass().getResource("/server/config.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			Config config = gson.fromJson(content, Config.class);
			configRepository.save(config);
		}
	}

	/**
	 * This function adds default queries for each group which is created
	 *
	 * @param probeId
	 * @throws IOException
	 */
	public void addDefaultGroupQueries(String groupId) throws IOException {
		List<QueryRun> queriesDB = queryRepository.findByGroupId(groupId, true);

		if (queriesDB == null || queriesDB.isEmpty()) {

			File file = new File(getClass().getResource("/server/queries.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			QueryRun[] queries = gson.fromJson(content, QueryRun[].class);

			List<QueryRun> queryList = Arrays.asList(queries);

			for (QueryRun query : queryList) {
				query.setGroupId(groupId);
				queryRepository.save(query);
			}
		}
	}

	/**
	 * This function adds default processors for each group which is created
	 *
	 * @param user_id
	 * @throws IOException
	 */
	public void addDefaultGroupProcessors(String groupId) throws IOException {
		List<Processor> processorsDB = processorRepository.getProcessorsByGroupId(groupId);

		if (processorsDB == null || processorsDB.isEmpty()) {

			File file = new File(getClass().getResource("/server/processors.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			Processor[] processorsArray = gson.fromJson(content, Processor[].class);
			List<Processor> processors = Arrays.asList(processorsArray);
			for (Processor processor : processors) {
				processor.setGroupId(groupId);
				processorRepository.save(processor);
			}
		}
	}

	/**
	 * This function adds default settings at the system startup if settings does not exist in the Portal DB
	 *
	 * @throws IOException
	 *
	 */
	public void addDefaultSettings() throws IOException {
		List<Settings> settingsDB = settingsRepository.findAll();

		if (settingsDB == null || settingsDB.isEmpty()) {

			File file = new File(getClass().getResource("/server/settings.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			Settings settings = gson.fromJson(content, Settings.class);
			settingsRepository.save(settings);
		}
	}

	public void addDefaultLicensePlan() throws IOException {
		List<LicensePlan> licensePlansDB = licensePlanRepository.findAll();

		if (licensePlansDB == null || licensePlansDB.isEmpty()) {

			File file = new File(getClass().getResource("/server/licensePlan.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			LicensePlan licensePlan = gson.fromJson(content, LicensePlan.class);
			licensePlanRepository.save(licensePlan);
		}
	}

	/**
	 * This function adds default steps for the new licensed probe
	 *
	 * @param user_id
	 * @throws IOException
	 */
	public void addDefaultGroupSteps(String groupId) throws IOException {
		List<Step> stepsDB = stepRepository.getStepsByGroupId(groupId, true);
		if (stepsDB == null || stepsDB.isEmpty()) {

			File file = new File(getClass().getResource("/server/steps.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			Step[] stepArray = gson.fromJson(content, Step[].class);

			List<Step> steps = Arrays.asList(stepArray);
			for (Step step : steps) {
				List<Step> stepsDBsize = stepRepository.getStepsByGroupId(groupId, true);
				if (stepsDBsize == null || stepsDBsize.isEmpty()) {
					step.setNumber(1);
				} else {
					step.setNumber(stepsDBsize.size() + 1);
				}
				step.setGroupId(groupId);
				step.setActive(1);
				stepRepository.save(step);
			}
		}
	}

	/**
	 * This function adds default users in case that those are not already added to the database
	 *
	 * @throws IOException
	 */
	public void addDefaultUsers() throws IOException {
		UserRegistration userRegistration = new UserRegistration();
		for (String email : StaticConfigItems.SECINTO_EMAIL_LIST) {
			User user = userRepository.findByEmail(email);
			if (user == null) {
				userRegistration.setEmail(email);
				userRegistration.setRegistrationType(UserRegistrationType.INITIALIZATION);
				userRegistration.setUserRole(UserRole.SUPERADMIN);
				userUtils.initializeSecintoUsers(userRegistration, "en");
			}
		}
	}
}
