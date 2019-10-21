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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.GroupUtils;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	GroupUtils groupUtils;

	RestTemplate restTemplate = new RestTemplate();

	static final Logger log = LoggerFactory.getLogger(ConfigController.class);

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
