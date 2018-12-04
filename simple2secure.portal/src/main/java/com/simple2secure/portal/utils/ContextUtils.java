package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.User;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.ContextRepository;
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
public class ContextUtils {

	private static Logger log = LoggerFactory.getLogger(ContextUtils.class);

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
	ContextRepository contextRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	/**
	 * This function returns all contexts which are created by the user or assigned to.
	 *
	 * @param context
	 * @return
	 */
	public List<Context> getContextsByUserId(User user) {
		log.debug("Retrieving contexts for the user {}", user.getEmail());
		List<Context> myContexts = new ArrayList<Context>();

		if (user != null) {
			List<ContextUserAuthentication> contextUserAuthList = contextUserAuthRepository.getByUserId(user.getId());
			if (contextUserAuthList != null) {
				for (ContextUserAuthentication contextUserAuth : contextUserAuthList) {
					if (contextUserAuth != null) {
						Context context = contextRepository.find(contextUserAuth.getContextId());
						if (context != null) {
							myContexts.add(context);
						}
					}
				}
			}
		}

		return myContexts;
	}

}
