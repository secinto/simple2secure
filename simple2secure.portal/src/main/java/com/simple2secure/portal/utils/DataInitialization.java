package com.simple2secure.portal.utils;
import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.simple2secure.api.config.ConfigItems;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Config;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.Step;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.repository.StepRepository;

@Service
public class DataInitialization {
	
	private static Logger log = LoggerFactory.getLogger(DataInitialization.class);
	
	static RestTemplate restTemplate = new RestTemplate();
	
	/**
	 * 
	 * @param userId
	 * @param username
	 * 
	 * This function adds a default group for the users which are registered using the standard
	 * registration. This function does not apply when another user(superadmin, admin, superuser) 
	 * adds new user, because he has to choose the group while adding.
	 */
	public static void addDefaultGroup(String userId, String username) {
		GroupRepository groupRepository = BeanUtil.getBean(GroupRepository.class);
		
		List<CompanyGroup> groupList = groupRepository.findByOwnerId(userId);
		
		if(groupList == null || groupList.isEmpty()) {
			ResponseEntity<CompanyGroup> response = restTemplate.getForEntity(ConfigItems.group_url, CompanyGroup.class);
			CompanyGroup group = response.getBody();
			group.setAddedByUserId(userId);
			String expirationDate = PortalUtils.getDefaultLicenseExpirationDate();
			group.setOwner(username);
			group.setLicenseExpirationDate(expirationDate);
			ObjectId groupId = groupRepository.saveAndReturnId(group);
			if(!Strings.isNullOrEmpty(groupId.toString())) {
				addDefaultGroupConfiguration(groupId.toString());
				addDefaultGroupQueries(groupId.toString());
				addDefaultGroupProcessors(groupId.toString());
				addDefaultGroupSteps(groupId.toString());
			}
		}
	}
	
	/**
	 * This function adds default configuration for each group which is created
	 * @param probeId
	 */
	public static void addDefaultGroupConfiguration(String groupId) {
		ConfigRepository configRepository = BeanUtil.getBean(ConfigRepository.class);
		Config config = configRepository.findByGroupId(groupId);
		if (config == null) {
			ResponseEntity<Config> response = restTemplate.getForEntity(ConfigItems.config_url, Config.class);
			Config configuration = response.getBody();
			configuration.setGroupConfiguration(true);
			configuration.setGroupId(groupId);
			configRepository.save(configuration);
		}
	}
	
	/**
	 * This function adds default queries for each group which is created
	 * @param probeId
	 */
	public static void addDefaultGroupQueries(String groupId) {
		QueryRepository queryRepository = BeanUtil.getBean(QueryRepository.class);
		List<QueryRun> queriesDB = queryRepository.findByGroupId(groupId, true, true);

		if (queriesDB == null || queriesDB.isEmpty()) {
			ResponseEntity<QueryRun[]> response = restTemplate.getForEntity(ConfigItems.query_url, QueryRun[].class);
			List<QueryRun> queries = Arrays.asList(response.getBody());

			for (QueryRun query : queries) {
				query.setGroupId(groupId);
				query.setGroupQueryRun(true);
				queryRepository.save(query);
			}
		}		
	}
	
	/**
	 * This function adds default processors for each group which is created
	 *
	 * @param user_id
	 */
	public static void addDefaultGroupProcessors(String groupId) {
		ProcessorRepository processorRepository = BeanUtil.getBean(ProcessorRepository.class);
		List<Processor> processorsDB = processorRepository.getProcessorsByGroupId(groupId, true);

		if (processorsDB == null || processorsDB.isEmpty()) {
			ResponseEntity<Processor[]> response = restTemplate.getForEntity(ConfigItems.processors_url, Processor[].class);
			List<Processor> processors = Arrays.asList(response.getBody());
			for (Processor processor : processors) {
				processor.setGroupId(groupId);
				processor.setGroupProcessor(true);
				processorRepository.save(processor);
			}
		}
	}
	
	/**
	 * This function adds default settings at the system startup if settings does not exist in the Portal DB
	 *
	 */
	public static void addDefaultSettings() {
		SettingsRepository settingsRepository = BeanUtil.getBean(SettingsRepository.class);
		List<Settings> settingsDB = settingsRepository.findAll();
		
		if(settingsDB == null || settingsDB.isEmpty()) {
			ResponseEntity<Settings> response = restTemplate.getForEntity(ConfigItems.settings_url, Settings.class);
			Settings settings = response.getBody();
			settingsRepository.save(settings);
		}
		
	}	
	
	/**
	 * This function adds default steps for the new licensed probe
	 *
	 * @param user_id
	 */
	public static void addDefaultGroupSteps(String groupId) {
		StepRepository stepRepository = BeanUtil.getBean(StepRepository.class);
		List<Step> stepsDB = stepRepository.getStepsByGroupId(groupId, true, true);
		if (stepsDB == null || stepsDB.isEmpty()) {
			ResponseEntity<Step[]> response = restTemplate.getForEntity(ConfigItems.steps_url, Step[].class);
			List<Step> steps = Arrays.asList(response.getBody());
			for (Step step : steps) {
				List<Step> stepsDBsize = stepRepository.getStepsByGroupId(groupId, true, true);
				if (stepsDBsize == null || stepsDBsize.isEmpty()) {
					step.setNumber(1);
				} else {
					step.setNumber(stepsDBsize.size() + 1);
				}
				step.setGroupId(groupId);
				step.setActive(1);
				step.setGroupStep(true);
				stepRepository.save(step);
			}
		}
	}
	
	/**
	 * This function adds new default configuration for the new licensed probe
	 *
	 * @param userUUID
	 */
	public static boolean addConfiguration(String probeId, String groupId) {
		ConfigRepository configRepository = BeanUtil.getBean(ConfigRepository.class);
		Config config = configRepository.findByGroupId(groupId);
		if (config == null) {
			log.error("Config for the specified group not found");
			return false;
		}
		else {
			config.setId(null);
			config.setVersion(1);
			config.setProbeId(probeId);
			config.setGroupConfiguration(false);
			configRepository.save(config);
			return true;
		}
	}
	
	/**
	 * This function adds default processors for the new licensed probe
	 *
	 * @param user_id
	 */
	public static boolean addProcessors(String probeId, String groupId) {
		ProcessorRepository processorRepository = BeanUtil.getBean(ProcessorRepository.class);
		List<Processor> processors = processorRepository.getProcessorsByGroupId(groupId, true);

		if (processors == null) {
			log.error("Processors for the specified group not found");
			return false;
		}
		else {
			for(Processor processor : processors) {
				processor.setId(null);
				processor.setProbeId(probeId);
				processor.setGroupProcessor(false);
				processorRepository.save(processor);
			}
			
			return true;
		}
	}

	/**
	 * This function adds default queries for the new licensed probe
	 *
	 * @param user_uuid
	 */
	public static boolean addQueries(String probeId, String groupId) {
		QueryRepository queryRepository = BeanUtil.getBean(QueryRepository.class);
		List<QueryRun> queries = queryRepository.findByGroupId(groupId, true, true);

		if (queries == null) {
			log.error("Queries for the specified group not found");
			return false;
		}
		else {
			for (QueryRun query : queries) {
				query.setId(null);
				query.setProbeId(probeId);
				query.setGroupQueryRun(false);
				queryRepository.save(query);
			}
			return true;
		}
	}

	/**
	 * This function adds default steps for the new licensed probe
	 *
	 * @param user_id
	 */
	public static boolean addSteps(String probeId, String groupId) {
		StepRepository stepRepository = BeanUtil.getBean(StepRepository.class);
		List<Step> steps = stepRepository.getStepsByGroupId(groupId, true, true);
		if (steps == null) {
			log.error("Steps for the specified group not found");
			return false;
		}
		else {
			for (Step step : steps) {
				step.setId(null);
				step.setProbeId(probeId);
				step.setActive(1);
				step.setGroupStep(false);
				stepRepository.save(step);
			}
			return true;
		}
	}	
}
