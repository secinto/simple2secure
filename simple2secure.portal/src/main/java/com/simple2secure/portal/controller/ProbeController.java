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
import com.simple2secure.api.model.Probe;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.GroupUtils;

@RestController
@RequestMapping("/api/probe")
public class ProbeController {

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

	/**
	 * This function returns all devices according to the user id
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/changeGroup/{probeId}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<CompanyLicensePrivate> changeGroupProbe(@PathVariable("probeId") String probeId, @RequestBody CompanyGroup group,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(probeId) && group != null) {
			// retrieve license from database
			CompanyLicensePrivate license = licenseRepository.findByProbeId(probeId);
			CompanyGroup dbGroup = groupRepository.find(group.getId());
			if (license != null && dbGroup != null) {

				license.setGroupId(dbGroup.getId());
				// TODO - check what needs to be updated in order that probe gets a correct
				// during the next license check
				licenseRepository.update(license);
				return new ResponseEntity<>(license, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_probe_group", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all devices according to the user id
	 */
	@RequestMapping(value = "/{userID}")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Probe>> getProbesByUserID(@PathVariable("userID") String userId,
			@RequestHeader("Accept-Language") String locale) {

		List<Probe> probes = new ArrayList<Probe>();

		List<CompanyLicensePrivate> licenses = licenseRepository.findByUserId(userId);

		if (licenses != null) {
			for (CompanyLicensePrivate license : licenses) {
				// Retrieve only activated probes
				if (license.isActivated()) {
					CompanyGroup group = groupRepository.find(license.getGroupId());
					if (group != null) {
						Probe probe = new Probe(license.getProbeId(), group, license.isActivated());
						probes.add(probe);
					}
				}
			}
		}
		return new ResponseEntity<List<Probe>>(probes, HttpStatus.OK);
	}

	/**
	 * This function returns all devices according to the user id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/deleteProbe/{probeId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<CompanyLicensePrivate> deleteProbe(@PathVariable("probeId") String probeId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(probeId)) {
			// retrieve license from database
			CompanyLicensePrivate license = licenseRepository.findByProbeId(probeId);
			if (license != null) {
				// TODO - check before deleting if we need to decrement the number of downloaded licenses in context
				licenseRepository.delete(license);
				return new ResponseEntity<>(license, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_probe", locale)),
				HttpStatus.NOT_FOUND);
	}

}
