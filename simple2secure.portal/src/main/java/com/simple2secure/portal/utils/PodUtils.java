package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.PodDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Pod;
import com.simple2secure.api.model.Test;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.ReportRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@Component
public class PodUtils {

	private static Logger log = LoggerFactory.getLogger(PodUtils.class);

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
	TestRepository testRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	/**
	 * This function returns all pods from the current context
	 *
	 * @param context
	 * @return
	 */
	public List<Pod> getAllPodsFromCurrentContext(Context context) {
		log.debug("Retrieving pods for the context {}", context.getName());
		/* Set user probes from the licenses - not from the users anymore */
		List<Pod> myPods = new ArrayList<Pod>();
		List<CompanyGroup> assignedGroups = groupRepository.findByContextId(context.getId());
		for (CompanyGroup group : assignedGroups) {
			List<CompanyLicensePrivate> licenses = licenseRepository.findByGroupId(group.getId());
			if (licenses != null) {
				for (CompanyLicensePrivate license : licenses) {
					if (license.isActivated()) {
						if (!Strings.isNullOrEmpty(license.getPodId())) {
							Pod pod = new Pod(license.getPodId(), group, license.isActivated(), license.getHostname());
							myPods.add(pod);
						}
					}
				}
			}
		}
		log.debug("Retrieved {0} pods for context {1}", myPods.size(), context.getName());
		return myPods;
	}

	/**
	 * This function returns all pods from the current context
	 *
	 * @param context
	 * @return
	 */
	public List<PodDTO> getAllPodsFromCurrentContextWithTests(Context context) {
		log.debug("Retrieving pods for the context {}", context.getName());
		/* Set user probes from the licenses - not from the users anymore */
		List<PodDTO> myPods = new ArrayList<PodDTO>();
		List<CompanyGroup> assignedGroups = groupRepository.findByContextId(context.getId());
		for (CompanyGroup group : assignedGroups) {
			List<CompanyLicensePrivate> licenses = licenseRepository.findByGroupId(group.getId());
			if (licenses != null) {
				for (CompanyLicensePrivate license : licenses) {
					if (license.isActivated()) {
						if (!Strings.isNullOrEmpty(license.getPodId())) {
							Pod pod = new Pod(license.getPodId(), group, license.isActivated(), license.getHostname());
							List<Test> tests = testRepository.getByPodId(pod.getPodId());

							PodDTO podDto = new PodDTO(pod, tests);
							myPods.add(podDto);
						}
					}
				}
			}
		}
		log.debug("Retrieved {0} pods for context {1}", myPods.size(), context.getName());
		return myPods;
	}

	public void deletePodDependencies(String podId) {
		if (!Strings.isNullOrEmpty(podId)) {
			// TODO - check before deleting if we need to decrement the number of downloaded licenses in context
			licenseRepository.deleteByPodId(podId);
			log.debug("Deleted dependencies for probe id {}", podId);
		}
	}

}
