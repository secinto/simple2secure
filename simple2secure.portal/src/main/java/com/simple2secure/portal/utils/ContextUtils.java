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

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.CurrentContext;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ContextUtils extends BaseServiceProvider {

	@Autowired
	GroupUtils groupUtils;

	@Autowired
	MailUtils mailUtils;

	@Autowired
	UserUtils userUtils;

	/**
	 * This function maps all superadmins to the new context which has been added so that they have full control of each context.
	 *
	 * @param contextId
	 */
	public void mapSuperAdminsTotheContext(ObjectId contextId) {
		// TODO: update it with the keycloak accordingly
		List<String> superAdminIdList = new ArrayList<>();
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
	public ObjectId addContextUserAuthentication(String userId, ObjectId contextId, UserRole userRole, boolean defaultContext) {

		if (!Strings.isNullOrEmpty(userId) && userRole != null) {
			ContextUserAuthentication contextUserAuthentication = new ContextUserAuthentication(userId, contextId, userRole, defaultContext);

			List<ContextUserAuthentication> contextUserAuthList = contextUserAuthRepository.getByUserId(userId);

			ObjectId contextUserAuthenticationId = contextUserAuthRepository.saveAndReturnId(contextUserAuthentication);
			if (contextUserAuthList.size() == 0) {
				CurrentContext currentContext = new CurrentContext(userId, contextUserAuthenticationId);
				currentContextRepository.save(currentContext);
			}
			return contextUserAuthenticationId;

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
	public ObjectId addNewContextForRegistration(String email, String userId) {
		LicensePlan licensePlan = licensePlanRepository.findByName(StaticConfigItems.DEFAULT_LICENSE_PLAN);

		if (licensePlan != null) {
			Context context = new Context();
			context.setName(generateContextName(email));
			context.setLicensePlanId(licensePlan.getId());

			log.debug("Added new context with name: {}", context.getName());

			return contextRepository.saveAndReturnId(context);

		} else {
			log.error("License Plan {} not found", StaticConfigItems.DEFAULT_LICENSE_PLAN);
		}
		return null;
	}

	/**
	 * This function deletes all context dependencies
	 *
	 * @param contextId
	 */
	public void deleteContextDependencies(Context context) {
		// delete all currentContext mappings
		currentContextRepository.deleteByContextId(context.getId());
		// delete all contextUserAuthentication mappings
		contextUserAuthRepository.deleteByContextId(context.getId());
		// delete all groups from this context
		groupUtils.deleteGroupsByContextId(context.getId());
		// delete email configuration and all dependencies accordingly
		mailUtils.deleteEmailConfigurationByContextId(context.getId());
		// delete notification
		notificationRepository.deleteByContextId(context.getId());
		// delete user invitation
		userInvitationRepository.deleteByContexId(context.getId());

		// delete context
		contextRepository.delete(context);
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

	/**
	 * This function checks if the current user can delete the provided context
	 *
	 * @param user
	 * @param context
	 * @return
	 */
	public boolean checkIfUserCanDeleteContext(String userId, Context context) {
		if (context != null) {
			ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(context.getId(), userId);
			if (contextUserAuthentication.getUserRole().equals(UserRole.SUPERADMIN)) {
				return true;
			}
			// ADMIN CAN ONLY DELETE OWN CONTEXTS (those that he has been created)
			else if (contextUserAuthentication.getUserRole().equals(UserRole.ADMIN)) {
				if (contextUserAuthentication.isOwnContext()) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * This function returns all contexts which are created by the user or assigned to.
	 *
	 * @param context
	 * @return
	 */
	public List<Context> getContextsByUserId(String userId) {
		log.debug("Retrieving contexts for the user {}", userId);
		List<Context> myContexts = new ArrayList<>();

		if (!Strings.isNullOrEmpty(userId)) {
			List<ContextUserAuthentication> contextUserAuthList = contextUserAuthRepository.getByUserId(userId);
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
	 * Method to get the contextId by a given devideId.
	 *
	 * @param deviceId
	 * @return ObjectId
	 */
	public ObjectId getContextIdFromDeviceId(ObjectId deviceId) {
		ObjectId groupId = licenseRepository.findByDeviceId(deviceId).getGroupId();
		CompanyGroup companyGroup = groupRepository.find(groupId);
		ObjectId contextId = companyGroup.getContextId();

		return contextId;
	}

	/**
	 * Method to get the contextId by a given OsQueryReport.
	 *
	 * @param report
	 * @return ObjectId
	 */
	public ObjectId getContextIdFromOsQueryReport(OsQueryReport report) {
		return getContextIdFromDeviceId(report.getDeviceId());
	}

	/**
	 * Method to get the contextId by a given NetworkReport.
	 *
	 * @param report
	 * @return ObjectId
	 */
	public ObjectId getContextIdFromNetworkReport(NetworkReport report) {
		return getContextIdFromDeviceId(report.getDeviceId());
	}

	/**
	 * Method to get the contextId by a given TestResult.
	 *
	 * @param result
	 * @return ObjectId
	 */
	public ObjectId getContextIdFromTestResult(TestResult result) {
		TestRun testRun = testRunRepository.find(result.getTestRunId());
		return getContextIdFromDeviceId(testRun.getPodId());
	}

	/**
	 * Method to get the contextId by a given TestSequenceResult.
	 *
	 * @param result
	 * @return ObjectId
	 */
	public ObjectId getContextIdFromTestSequenceResult(TestSequenceResult result) {
		return getContextIdFromDeviceId(result.getPodId());
	}

}
