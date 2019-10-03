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

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.GroupUtils;
import com.simple2secure.portal.utils.ProbeUtils;

@RestController
@RequestMapping("/api/probe")
public class ProbeController {

	public static final Logger log = LoggerFactory.getLogger(ProbeController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	GroupUtils groupUtils;

	@Autowired
	ProbeUtils probeUtils;

	/**
	 * This function returns all devices according to the user id
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/changeGroup/{probeId}",
			method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<CompanyLicensePrivate> changeGroupProbe(@PathVariable("probeId") String probeId, @RequestBody CompanyGroup group,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(probeId) && group != null) {
			// retrieve license from database
			CompanyLicensePrivate license = licenseRepository.findByDeviceId(probeId);
			CompanyGroup dbGroup = groupRepository.find(group.getId());
			if (license != null && dbGroup != null) {

				license.setGroupId(dbGroup.getId());
				// TODO - check what needs to be updated in order that probe gets a correct
				// during the next license check
				licenseRepository.update(license);
				return new ResponseEntity<>(license, HttpStatus.OK);
			}
		}

		log.error("Problem occured while updating probe group for probe id {}", probeId);

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_probe_group", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all devices according to the user id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/deleteProbe/{probeId}",
			method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<CompanyLicensePrivate> deleteProbe(@PathVariable("probeId") String probeId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(probeId)) {

			CompanyLicensePrivate license = licenseRepository.findByDeviceId(probeId);

			if (license != null) {
				// delete All Probe dependencies
				probeUtils.deleteProbeDependencies(probeId);
				return new ResponseEntity<>(license, HttpStatus.OK);
			}
		}

		log.error("Problem occured while deleting probe with id {}", probeId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_probe", locale)),
				HttpStatus.NOT_FOUND);
	}

}
