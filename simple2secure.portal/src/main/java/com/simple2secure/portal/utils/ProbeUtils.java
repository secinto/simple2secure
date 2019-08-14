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
package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Probe;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.ReportRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@Component
public class ProbeUtils {

	private static Logger log = LoggerFactory.getLogger(ProbeUtils.class);

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	ProcessorRepository processorRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	NetworkReportRepository networkReportRepository;

	@Autowired
	QueryRepository queryRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	/**
	 * This function returns all probes from the current context
	 *
	 * @param context
	 * @return
	 */
	public List<Probe> getAllProbesFromCurrentContext(Context context) {
		log.debug("Retrieving probes for the context {}", context.getName());
		/* Set user probes from the licenses - not from the users anymore */
		List<Probe> myProbes = new ArrayList<>();
		List<CompanyGroup> assignedGroups = groupRepository.findByContextId(context.getId());
		for (CompanyGroup group : assignedGroups) {
			List<CompanyLicensePrivate> licenses = licenseRepository.findByGroupId(group.getId());
			if (licenses != null) {
				for (CompanyLicensePrivate license : licenses) {
					if (license.isActivated()) {
						if (!Strings.isNullOrEmpty(license.getProbeId())) {
							Probe probe = new Probe(license.getProbeId(), group, license.isActivated());
							myProbes.add(probe);
						}
					}
				}
			}
		}
		log.debug("Retrieved {0} probes for context {1}", myProbes.size(), context.getName());
		return myProbes;
	}

	public void deleteProbeDependencies(String probeId) {
		if (!Strings.isNullOrEmpty(probeId)) {
			// TODO - check before deleting if we need to decrement the number of downloaded licenses in context
			licenseRepository.deleteByProbeId(probeId);
			log.debug("Deleted dependencies for probe id {}", probeId);
		}

	}

}
