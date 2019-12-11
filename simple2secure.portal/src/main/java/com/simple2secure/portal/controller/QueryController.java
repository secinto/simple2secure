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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.OsQueryDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.OSInfo;
import com.simple2secure.api.model.OsQuery;
import com.simple2secure.api.model.OsQueryCategory;
import com.simple2secure.api.model.OsQueryGroupMapping;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputDevice;
import simple2secure.validator.model.ValidInputGroup;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputOsinfo;
import simple2secure.validator.model.ValidInputQuery;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.QUERY_API)
public class QueryController extends BaseController {

	static final Logger log = LoggerFactory.getLogger(QueryController.class);

	/**
	 * This function returns a {@link OsQuery} by the specified Id.
	 *
	 * @param id
	 *          The id of the desired {@link OsQuery}
	 * @param locale
	 *          The currently selected locale
	 * @return
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<OsQuery> getQueryByID(@PathVariable ValidInputQuery queryId, @ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(queryId.getValue())) {
			OsQuery queryConfig = queryRepository.find(queryId.getValue());
			if (queryConfig != null) {
				return new ResponseEntity<>(queryConfig, HttpStatus.OK);
			}
		}
		log.error("Query configuration not found for the query ID {}", queryId.getValue());
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("queryrun_not_found", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@ValidRequestMapping(
			value = "/allDto")
	public ResponseEntity<List<OsQueryDTO>> getQueryDTOs(@ServerProvidedValue ValidInputLocale locale) {

		List<OsQueryDTO> queryDtoList = new ArrayList<>();
		List<OsQueryCategory> categories = queryCategoryRepository.findAll();
		if (categories != null) {
			for (OsQueryCategory category : categories) {
				List<OsQuery> queryRunList = queryRepository.findByCategoryId(category.getId());

				queryDtoList.add(new OsQueryDTO(category, queryRunList));
			}

			return new ResponseEntity<>(queryDtoList, HttpStatus.OK);
		}

		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_queryrun", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@ValidRequestMapping(
			value = "/unmapped")
	public ResponseEntity<List<OsQuery>> getUnmappedQueriesByGroupId(@PathVariable ValidInputGroup groupId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(groupId.getValue())) {
			List<OsQuery> queryList = queryUtils.getUnmappedQueriesByGroup(groupId.getValue());

			if (queryList != null) {
				return new ResponseEntity<>(queryList, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_queryrun", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function updates or saves new Query config into the database
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<OsQuery> updateQuery(@RequestBody OsQuery query, @ServerProvidedValue ValidInputLocale locale)
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
	 * This function updates or saves new Query config into the database
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/category",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<OsQueryCategory> updateSaveCategory(@RequestBody OsQueryCategory category,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (category != null) {
			if (!Strings.isNullOrEmpty(category.getId())) {

				queryCategoryRepository.update(category);
				return new ResponseEntity<>(category, HttpStatus.OK);
			}

			else {

				// TODO: Check if category by name already exists
				queryCategoryRepository.save(category);
				return new ResponseEntity<>(category, HttpStatus.OK);
			}
		}
		log.error("Error while inserting/updating queryRun");
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_update_queryrun", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function updates or saves new Query config into the database
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/mapping",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<OsQuery>> updateQueryMappings(@RequestBody List<OsQuery> queryList, @PathVariable ValidInputGroup groupId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (queryList != null && !Strings.isNullOrEmpty(groupId.getValue())) {
			CompanyGroup group = groupRepository.find(groupId.getValue());
			if (group != null) {
				queryUtils.updateQueryGroupMapping(queryList, groupId.getValue());
				return new ResponseEntity<>(queryList, HttpStatus.OK);
			}
		}
		log.error("Error while inserting/updating queryRun");
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_update_queryrun", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function returns all users from the user repository
	 */
	@ValidRequestMapping(
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<?> deleteQuery(@PathVariable ValidInputQuery queryId, @ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(queryId.getValue())) {
			OsQuery queryRun = queryRepository.find(queryId.getValue());
			if (queryRun != null) {
				queryGroupMappingRepository.deleteByQueryId(queryId.getValue());
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
	public ResponseEntity<List<OsQuery>> getQueriesByDeviceId(@PathVariable ValidInputDevice deviceId, @PathVariable ValidInputOsinfo osinfo,
			@ServerProvidedValue ValidInputLocale locale) {

		if (Strings.isNullOrEmpty(osinfo.getValue())) {
			osinfo.setValue(OSInfo.UNKNOWN.name());
		}

		if (!Strings.isNullOrEmpty(deviceId.getValue())) {
			CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId.getValue());
			if (license != null) {
				CompanyGroup group = groupRepository.find(license.getGroupId());
				if (group != null) {

					List<OsQuery> queryConfig = new ArrayList<>();

					// Check if this is root group
					if (group.isRootGroup()) {
						// Take only the query runs of this group, because this is root group!
						queryConfig = queryUtils.findByGroupIdAndOsInfo(group.getId(), OSInfo.valueOf(osinfo.getValue()));
					} else {
						// go until the root group is not found and get all configurations from all groups which are parents of this group
						List<CompanyGroup> foundGroups = portalUtils.findAllParentGroups(group);
						if (foundGroups != null) {
							List<String> groupIds = portalUtils.extractIdsFromObjects(foundGroups);
							if (groupIds != null) {
								queryConfig = queryUtils.findByGroupIdsAndOsInfo(groupIds, OSInfo.valueOf(osinfo.getValue()));
							}
						}
					}
					if (queryConfig != null) {
						queryConfig.sort(Comparator.comparing(OsQuery::getName, String.CASE_INSENSITIVE_ORDER));
						return new ResponseEntity<>(queryConfig, HttpStatus.OK);
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
	@ValidRequestMapping(
			value = "/group")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<OsQuery>> getQueriesByGroupId(@PathVariable ValidInputGroup groupId, @RequestParam boolean select_all,
			@ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(groupId.getValue())) {
			List<OsQuery> queries = new ArrayList<>();
			List<OsQueryGroupMapping> groupMappings = queryGroupMappingRepository.findByGroupId(groupId.getValue());

			if (groupMappings != null) {
				for (OsQueryGroupMapping mapping : groupMappings) {
					OsQuery query = queryRepository.find(mapping.getQueryId());
					if (query != null) {
						query = queryUtils.setValuesFromMapping(query, mapping);
						queries.add(query);
					}
				}
			}

			if (queries != null) {

				Collections.sort(queries, new Comparator<OsQuery>() {
					@Override
					public int compare(OsQuery u1, OsQuery u2) {
						return u1.getName().compareTo(u2.getName());
					}
				});

				return new ResponseEntity<>(queries, HttpStatus.OK);
			}
		}
		log.error("Query configuration not found for the group ID {}", groupId);
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("queryrun_not_found", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}
}
