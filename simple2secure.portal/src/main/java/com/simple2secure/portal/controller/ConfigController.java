/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Config;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.api.model.Step;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.PortalUtils;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	QueryRepository queryRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	ProcessorRepository processorRepository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	PortalUtils portalUtils;

	RestTemplate restTemplate = new RestTemplate();

	static final Logger log = LoggerFactory.getLogger(ConfigController.class);

	/**
	 * This function updates configuration and automatically after each update the version will be incremented
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN')")
	public ResponseEntity<Config> saveConfig(@RequestBody Config config, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {
		config.setVersion(config.getVersion() + 1);
		configRepository.update(config);
		return new ResponseEntity<Config>(config, HttpStatus.OK);
	}

	/**
	 * this function returns config by config id
	 *
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'PROBE')")
	public ResponseEntity<Config> getConfig(@RequestHeader("Accept-Language") String locale) {
		List<Config> configs = configRepository.findAll();
		if (configs == null || configs.size() != 1) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("configuration_not_found", locale)),
					HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Config>(configs.get(0), HttpStatus.OK);
	}

	/**
	 * This function updates or saves new Query config into the database
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<QueryRun> updateQuery(@RequestBody QueryRun query, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(query.getId())) {

			queryRepository.update(query);
			return new ResponseEntity<QueryRun>(query, HttpStatus.OK);
		}

		else {
			queryRepository.save(query);
			return new ResponseEntity<QueryRun>(query, HttpStatus.OK);
		}

	}

	/**
	 * This function returns all users from the user repository
	 */
	@RequestMapping(value = "/query/{queryId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<?> deleteConfig(@PathVariable("queryId") String queryId, @RequestHeader("Accept-Language") String locale) {
		QueryRun queryRun = queryRepository.find(queryId);
		if (queryRun == null) {
			return new ResponseEntity<>(
					new CustomErrorType(
							messageByLocaleService.getMessage("problem_occured_while_deleting_queryrun", ObjectUtils.toObjectArray(queryId), locale)),
					HttpStatus.NOT_FOUND);
		} else {
			queryRepository.delete(queryRun);
			return new ResponseEntity<>(queryRun, HttpStatus.OK);
		}
	}

	/**
	 * This function returns the query config for the specified user
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'PROBE')")
	@RequestMapping(value = "/query/{probeId}/{select_all}", method = RequestMethod.GET)
	public ResponseEntity<List<QueryRun>> getQueriesByUserID(@PathVariable("probeId") String probeId,
			@PathVariable("select_all") boolean select_all, @RequestHeader("Accept-Language") String locale) {

		CompanyLicensePrivate license = licenseRepository.findByProbeId(probeId);

		if (license != null) {

			CompanyGroup group = groupRepository.find(license.getGroupId());

			if (group != null) {
				// Check if this is root group
				List<QueryRun> queryConfig = new ArrayList<>();
				if (group.isRootGroup()) {
					// Take only the query runs of this group, because this is root group!
					queryConfig = queryRepository.findByGroupId(license.getGroupId(), select_all);
					if (queryConfig == null) {
						return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_queryrun", locale)),
								HttpStatus.NOT_FOUND);
					}

					return new ResponseEntity<List<QueryRun>>(queryConfig, HttpStatus.OK);
				} else {
					// go until the root group is not found and get all configurations from all groups which are parents of this group
					List<CompanyGroup> foundGroups = portalUtils.findAllParentGroups(group);

					// Iterate through all found groups and add their queries to the queryConfig
					if (foundGroups != null) {
						for (CompanyGroup cg : foundGroups) {
							List<QueryRun> currentQueries = queryRepository.findByGroupId(cg.getId(), select_all);
							if (currentQueries != null) {
								queryConfig.addAll(currentQueries);
							}
						}
					}

					return new ResponseEntity<List<QueryRun>>(queryConfig, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_queryrun", locale)),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_queryrun", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This function returns query config by the id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/query/group/{groupId}/{select_all}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<QueryRun>> getQueriesByGroupId(@PathVariable("groupId") String groupId,
			@PathVariable("select_all") boolean select_all, @RequestHeader("Accept-Language") String locale) {
		List<QueryRun> queryConfig = queryRepository.findByGroupId(groupId, select_all);

		if (queryConfig == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("queryrun_not_found", locale)), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<QueryRun>>(queryConfig, HttpStatus.OK);
	}

	/**
	 * This function returns query config by the id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<QueryRun> getQueryByID(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {
		QueryRun queryConfig = queryRepository.find(id);

		if (queryConfig == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("queryrun_not_found", locale)), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<QueryRun>(queryConfig, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/copy/{sourceGroupId}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> copyGroupConfiguration(@RequestBody CompanyGroup destGroup,
			@PathVariable("sourceGroupId") String sourceGroupId, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		CompanyGroup sourceGroup = groupRepository.find(sourceGroupId);

		if (sourceGroup != null && destGroup != null) {
			if (!Strings.isNullOrEmpty(destGroup.getId())) {
				// Cannot copy if both group IDs are same
				if (!sourceGroupId.equals(destGroup.getId())) {
					// Delete all configuration from destGroup
					List<QueryRun> queries = queryRepository.findByGroupId(destGroup.getId(), true);
					List<Processor> processors = processorRepository.getProcessorsByGroupId(destGroup.getId());
					List<Step> steps = stepRepository.getStepsByGroupId(destGroup.getId(), true);

					if (queries != null) {
						for (QueryRun query : queries) {
							queryRepository.delete(query);
						}
					}

					if (processors != null) {
						for (Processor processor : processors) {
							processorRepository.delete(processor);
						}
					}

					if (steps != null) {
						for (Step step : steps) {
							stepRepository.delete(step);
						}
					}

					// Copy all configurations from the source group to dest group

					queries = queryRepository.findByGroupId(sourceGroup.getId(), true);
					processors = processorRepository.getProcessorsByGroupId(sourceGroup.getId());
					steps = stepRepository.getStepsByGroupId(sourceGroup.getId(), true);

					if (queries != null) {
						for (QueryRun query : queries) {
							query.setGroupId(destGroup.getId());
							query.setId(null);
							queryRepository.save(query);
						}
					}

					if (processors != null) {
						for (Processor processor : processors) {
							processor.setGroupId(destGroup.getId());
							processor.setId(null);
							processorRepository.save(processor);
						}
					}

					if (steps != null) {
						for (Step step : steps) {
							step.setGroupId(destGroup.getId());
							step.setId(null);
							stepRepository.save(step);
						}
					}

					return new ResponseEntity<CompanyGroup>(sourceGroup, HttpStatus.OK);
				}
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_copying_config", locale)),
				HttpStatus.NOT_FOUND);
	}
}
