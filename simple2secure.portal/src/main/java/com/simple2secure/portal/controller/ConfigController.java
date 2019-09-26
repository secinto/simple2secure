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
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.GroupUtils;
import com.simple2secure.portal.utils.PortalUtils;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

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
	ContextRepository contextRepository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	GroupUtils groupUtils;

	RestTemplate restTemplate = new RestTemplate();

	static final Logger log = LoggerFactory.getLogger(ConfigController.class);

	/**
	 * This function updates or saves new Query config into the database
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(
			value = "/query",
			method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<QueryRun> updateQuery(@RequestBody QueryRun query, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (query != null) {
			if (!Strings.isNullOrEmpty(query.getId())) {

				queryRepository.update(query);
				return new ResponseEntity<>(query, HttpStatus.OK);
			}

			else {
				queryRepository.save(query);
				return new ResponseEntity<>(query, HttpStatus.OK);
			}
		}
		log.error("Error while inserting/updating queryRun");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_update_queryrun", locale)),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function returns all users from the user repository
	 */
	@RequestMapping(
			value = "/query/{queryId}",
			method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<?> deleteConfig(@PathVariable("queryId") String queryId, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(queryId)) {
			QueryRun queryRun = queryRepository.find(queryId);
			if (queryRun != null) {
				queryRepository.delete(queryRun);
				return new ResponseEntity<>(queryRun, HttpStatus.OK);
			}
		}
		log.error("Problem occured while deleting query run with id {}", queryId);
		return new ResponseEntity<>(
				new CustomErrorType(
						messageByLocaleService.getMessage("problem_occured_while_deleting_queryrun", ObjectUtils.toObjectArray(queryId), locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns the query config for the specified user
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'PROBE')")
	@RequestMapping(
			value = "/query/{probeId}/{select_all}",
			method = RequestMethod.GET)
	public ResponseEntity<List<QueryRun>> getQueriesByProbeId(@PathVariable("probeId") String probeId,
			@PathVariable("select_all") boolean select_all, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(probeId)) {
			CompanyLicensePrivate license = licenseRepository.findByProbeId(probeId);
			if (license != null) {
				CompanyGroup group = groupRepository.find(license.getGroupId());
				if (group != null) {

					List<QueryRun> queryConfig = new ArrayList<>();

					// Check if this is root group
					if (group.isRootGroup()) {
						// Take only the query runs of this group, because this is root group!
						queryConfig = queryRepository.findByGroupId(license.getGroupId(), select_all);
						if (queryConfig != null) {
							return new ResponseEntity<>(queryConfig, HttpStatus.OK);
						}

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

						return new ResponseEntity<>(queryConfig, HttpStatus.OK);
					}
				}
			}
		}
		log.error("Error while getting query run for the probe id {}", probeId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_queryrun", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns query config by the id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/query/group/{groupId}/{select_all}",
			method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<QueryRun>> getQueriesByGroupId(@PathVariable("groupId") String groupId,
			@PathVariable("select_all") boolean select_all, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(groupId)) {
			List<QueryRun> queryConfig = queryRepository.findByGroupId(groupId, select_all);

			if (queryConfig != null) {
				return new ResponseEntity<>(queryConfig, HttpStatus.OK);
			}
		}
		log.error("Query configuration not found for the group ID {}", groupId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("queryrun_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns query config by the id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/query/context/{contextId}/{select_all}",
			method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<QueryRun>> getQueriesByContextId(@PathVariable("contextId") String contextId,
			@PathVariable("select_all") boolean select_all, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {

			Context context = contextRepository.find(contextId);

			if (context != null) {

				List<CompanyGroup> groups = groupUtils.getAllGroupsByContextId(context);

				if (groups != null) {

					List<QueryRun> queryRunList = new ArrayList();
					for (CompanyGroup group : groups) {
						List<QueryRun> queryConfig = queryRepository.findByGroupId(group.getId(), select_all);
						if (queryConfig != null) {
							queryRunList.addAll(queryConfig);
						}
					}
					return new ResponseEntity<>(queryRunList, HttpStatus.OK);
				}

			}

		}
		log.error("Query configuration not found for the context ID {}", contextId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("queryrun_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns query config by the id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/query/{id}",
			method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<QueryRun> getQueryByID(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(id)) {
			QueryRun queryConfig = queryRepository.find(id);
			if (queryConfig != null) {
				return new ResponseEntity<>(queryConfig, HttpStatus.OK);
			}
		}
		log.error("Query configuration not found for the query ID {}", id);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("queryrun_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/copy/{sourceGroupId}",
			method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> copyGroupConfiguration(@RequestBody CompanyGroup destGroup,
			@PathVariable("sourceGroupId") String sourceGroupId, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {
		if (destGroup != null && !Strings.isNullOrEmpty(sourceGroupId)) {

			CompanyGroup sourceGroup = groupRepository.find(sourceGroupId);

			if (sourceGroup != null && destGroup != null) {
				if (!Strings.isNullOrEmpty(destGroup.getId())) {

					// Cannot copy if both group IDs are same
					if (!sourceGroupId.equals(destGroup.getId())) {

						// Delete all configuration from destGroup
						groupUtils.deleteGroup(destGroup.getId(), false);

						// Copy all configurations from the source group to dest group
						groupUtils.copyGroupConfiguration(sourceGroup.getId(), destGroup.getId());

						return new ResponseEntity<>(sourceGroup, HttpStatus.OK);
					}
				}
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_copying_config", locale)),
				HttpStatus.NOT_FOUND);
	}
}
