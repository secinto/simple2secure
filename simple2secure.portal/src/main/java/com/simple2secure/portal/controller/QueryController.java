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
import java.util.Comparator;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.OSInfo;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.commons.config.StaticConfigItems;
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

import simple2secure.validator.annotation.ValidInput;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputDevice;
import simple2secure.validator.model.ValidInputGroup;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputOsinfo;
import simple2secure.validator.model.ValidInputQuery;

@RestController
@RequestMapping(StaticConfigItems.QUERY_API)
public class QueryController {

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

	static final Logger log = LoggerFactory.getLogger(QueryController.class);

	/**
	 * This function updates or saves new Query config into the database
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<QueryRun> updateQuery(@RequestBody QueryRun query, @ValidInput ValidInputLocale locale)
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
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_update_queryrun", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function returns a {@link QueryRun} by the specified Id.
	 *
	 * @param id
	 *          The id of the desired {@link QueryRun}
	 * @param locale
	 *          The currently selected locale
	 * @return
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<QueryRun> getQueryByID(@PathVariable ValidInputQuery queryId, @ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(queryId.getValue())) {
			QueryRun queryConfig = queryRepository.find(queryId.getValue());
			if (queryConfig != null) {
				return new ResponseEntity<>(queryConfig, HttpStatus.OK);
			}
		}
		log.error("Query configuration not found for the query ID {}", queryId.getValue());
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("queryrun_not_found", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all users from the user repository
	 */
	@ValidRequestMapping(method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<?> deleteQuery(@PathVariable ValidInputQuery queryId, @ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(queryId.getValue())) {
			QueryRun queryRun = queryRepository.find(queryId.getValue());
			if (queryRun != null) {
				queryRepository.delete(queryRun);
				return new ResponseEntity<>(queryRun, HttpStatus.OK);
			}
		}
		log.error("Problem occured while deleting query run with id {}", queryId.getValue());
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_queryrun",
				ObjectUtils.toObjectArray(queryId), locale.getValue())), HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns the query config for the specified user
	 */
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'DEVICE')")
	@ValidRequestMapping
	public ResponseEntity<List<QueryRun>> getQueriesByDeviceId(@PathVariable ValidInputDevice deviceId, @PathVariable ValidInputOsinfo osinfo,
			@RequestParam boolean select_all, @ValidInput ValidInputLocale locale) {

		if (Strings.isNullOrEmpty(osinfo.getValue())) {
			osinfo.setValue(OSInfo.UNKNOWN.name());
		}

		if (!Strings.isNullOrEmpty(deviceId.getValue())) {
			CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId.getValue());
			if (license != null) {
				CompanyGroup group = groupRepository.find(license.getGroupId());
				if (group != null) {

					List<QueryRun> queryConfig = new ArrayList<>();

					// Check if this is root group
					if (group.isRootGroup()) {
						// Take only the query runs of this group, because this is root group!
						queryConfig = queryRepository.findByGroupIdAndOSInfo(license.getGroupId(), OSInfo.valueOf(osinfo.getValue()), select_all);
						if (queryConfig != null) {
							queryConfig.sort(Comparator.comparing(QueryRun::getName, String.CASE_INSENSITIVE_ORDER));
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
						if (queryConfig != null) {
							queryConfig.sort(Comparator.comparing(QueryRun::getName, String.CASE_INSENSITIVE_ORDER));
							return new ResponseEntity<>(queryConfig, HttpStatus.OK);
						}
					}
				}
			}
		}
		log.error("Error while getting query run for the probe id {}", deviceId);
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_queryrun", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns query config by the id
	 */
	@ValidRequestMapping(value = "/group")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<QueryRun>> getQueriesByGroupId(@PathVariable ValidInputGroup groupId, @RequestParam boolean select_all,
			@ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(groupId.getValue())) {
			List<QueryRun> queryConfig = queryRepository.findByGroupId(groupId.getValue(), select_all);

			if (queryConfig != null) {
				return new ResponseEntity<>(queryConfig, HttpStatus.OK);
			}
		}
		log.error("Query configuration not found for the group ID {}", groupId);
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("queryrun_not_found", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}
}
