package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
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
	LicensePlanRepository licensePlanRepository;

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

	/**
	 * This function maps all superadmins to the new context which has been added so that they have full control of each context.
	 *
	 * @param contextId
	 */
	public void mapSuperAdminsTotheContext(String contextId) {

		List<String> superAdminIdList = contextUserAuthRepository.getUserIdsByUserRole(UserRole.SUPERADMIN);
		if (superAdminIdList != null) {
			for (String superAdminId : superAdminIdList) {
				ContextUserAuthentication contextUserAuth = contextUserAuthRepository.getByContextIdAndUserId(contextId, superAdminId);
				if (contextUserAuth == null) {
					addContextUserAuthentication(superAdminId, contextId, UserRole.SUPERADMIN, false);
				}
			}
		}

	}

	/**
	 * This function adds new ContextUserAuthentication for each added user
	 *
	 * @param userId
	 * @param contextId
	 * @param userRole
	 */
	public ObjectId addContextUserAuthentication(String userId, String contextId, UserRole userRole, boolean defaultContext) {

		if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(userId) && userRole != null) {
			ContextUserAuthentication contextUserAuthentication = new ContextUserAuthentication(userId, contextId, userRole, defaultContext);

			return contextUserAuthRepository.saveAndReturnId(contextUserAuthentication);
		}
		return null;

	}

	/**
	 * This function checks if the context with the provided name already exists.
	 *
	 * @param context
	 * @param userId
	 * @return
	 */
	public boolean checkIfContextAlreadyExists(Context context, String userId) {
		List<ContextUserAuthentication> contextUserAuthList = contextUserAuthRepository.getByUserId(userId);
		if (contextUserAuthList != null) {
			for (ContextUserAuthentication contextUserAuth : contextUserAuthList) {
				Context contextFromDb = contextRepository.find(contextUserAuth.getContextId());
				if (contextFromDb != null) {
					if (contextFromDb.getName().trim().toLowerCase().equals(context.getName().trim().toLowerCase())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * This functions creates the new context for each user which is created using standard registration or which is automatically initialized
	 * on the startup.
	 *
	 * @param user
	 * @return
	 */
	public ObjectId addNewContextForRegistration(User user, ObjectId userId) {
		String licensePlanName = "Default";
		LicensePlan licensePlan = licensePlanRepository.findByName(licensePlanName);

		if (licensePlan != null) {
			Context context = new Context();
			context.setName(generateContextName(user.getEmail()));
			context.setLicensePlanId(licensePlan.getId());

			log.debug("Added new context with name: {}", context.getName());

			return contextRepository.saveAndReturnId(context);

		} else {
			log.error("License Plan {} not found", licensePlanName);
		}
		return null;
	}

	/**
	 * This function takes the user email and generates a context name from it. It takes part between "@" and last "." and adds current
	 * timestamp to it, user can change context name in the web.
	 *
	 * @param email
	 * @return
	 */
	public String generateContextName(String email) {

		String tempContextName = email.substring(email.indexOf("@") + 1);
		String contextName = tempContextName.substring(0, tempContextName.indexOf("."));
		contextName = contextName + "-" + System.currentTimeMillis();
		return contextName;
	}

}
