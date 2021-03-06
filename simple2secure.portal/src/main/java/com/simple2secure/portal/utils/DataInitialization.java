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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.OsQuery;
import com.simple2secure.api.model.OsQueryCategory;
import com.simple2secure.api.model.OsQueryGroupMapping;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.Step;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataInitialization extends BaseServiceProvider {

	@Autowired
	protected PortalUtils portalUtils;

	@Autowired
	protected UserUtils userUtils;

	@Autowired
	public RuleUtils ruleUtils;

	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;

	public Keycloak keycloak;

	/**
	 *
	 * @param userId
	 * @param username
	 *
	 *          This function adds a default group for the users which are registered using the standard registration. This function does not
	 *          apply when another user(superadmin, admin, superuser) adds new user, because he has to choose the group before adding.
	 * @throws IOException
	 */
	public void addDefaultGroup(String userId, ObjectId contextId) throws IOException {

		List<CompanyGroup> groupList = groupRepository.findByContextId(contextId);

		if (groupList == null || groupList.isEmpty()) {
			File file = new File(getClass().getResource("/server/group.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			CompanyGroup group = JSONUtils.fromString(content, CompanyGroup.class);

			if (group != null) {
				group.setContextId(contextId);
				group.setRootGroup(true);
				group.setStandardGroup(true);
				ObjectId groupId = groupRepository.saveAndReturnId(group);
				log.debug("Default group added for user with id {}", userId);
				if (!Strings.isNullOrEmpty(groupId.toString())) {
					mapActiveQueriesToDefaultGroup(groupId);
				}
			}
		}
	}

	/**
	 * This function adds default queries for each group which is created
	 *
	 * @param groupId
	 *          The group ID for which a default query should be created
	 * @throws IOException
	 */
	public void addDefaultQueries() throws IOException {
		List<OsQuery> queriesDB = queryRepository.findAll();

		if (queriesDB == null || queriesDB.isEmpty()) {

			File file = new File(getClass().getResource("/server/queries.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));

			JsonNode node = JSONUtils.fromString(content);

			if (node.isArray()) {
				ArrayNode arrayNode = (ArrayNode) node;
				Iterator<JsonNode> queryCategoryNode = arrayNode.elements();

				while (queryCategoryNode.hasNext()) {
					JsonNode currentCategoryNode = queryCategoryNode.next();
					OsQueryCategory category = portalUtils.generateQueryCategoryObjectFromJson(currentCategoryNode);
					ObjectId categoryId = queryCategoryRepository.saveAndReturnId(category);
					OsQuery[] queries = JSONUtils.fromString(JSONUtils.toString(currentCategoryNode.get("queries")), OsQuery[].class);

					if (queries != null) {
						List<OsQuery> queryList = Arrays.asList(queries);
						for (OsQuery query : queryList) {
							query.setCategoryId(categoryId);
							queryRepository.save(query);
						}
					}
				}
			}
		}
	}

	public void mapActiveQueriesToDefaultGroup(ObjectId groupId) {
		List<OsQuery> activeQueries = queryRepository.findByActiveStatus(1);
		if (activeQueries != null) {
			for (OsQuery query : activeQueries) {
				OsQueryGroupMapping queryGroupMapping = new OsQueryGroupMapping(groupId, query.getId(), query.getAnalysisInterval(),
						query.getAnalysisIntervalUnit(), query.getSystemsAvailable());
				queryGroupMappingRepository.save(queryGroupMapping);
			}
		}
	}

	/**
	 * This function adds default processors for each group which is created
	 *
	 * @param user_id
	 * @throws IOException
	 */
	public void addDefaultProcessors() throws IOException {
		List<Processor> processorsDB = processorRepository.findAll();

		if (processorsDB == null || processorsDB.isEmpty()) {

			File file = new File(getClass().getResource("/server/processors.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			Processor[] processorsArray = JSONUtils.fromString(content, Processor[].class);

			if (processorsArray != null) {
				List<Processor> processors = Arrays.asList(processorsArray);
				for (Processor processor : processors) {
					processorRepository.save(processor);
				}
			}
		}
	}

	/**
	 * This function adds default settings at the system startup if settings does not exist in the Portal DB
	 *
	 * @throws IOException
	 *
	 */
	public void addDefaultSettings() throws IOException {
		List<Settings> settingsDB = settingsRepository.findAll();

		if (settingsDB == null || settingsDB.isEmpty()) {

			File file = new File(getClass().getResource("/server/settings.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			Settings settings = JSONUtils.fromString(content, Settings.class);

			if (settings != null) {
				settingsRepository.save(settings);
			}
		}
	}

	/**
	 * This function adds default license plan, if no license plan exists, on the app initialization.
	 *
	 * @throws IOException
	 */
	public void addDefaultLicensePlan() throws IOException {
		List<LicensePlan> licensePlansDB = licensePlanRepository.findAll();

		if (licensePlansDB == null || licensePlansDB.isEmpty()) {

			File file = new File(getClass().getResource("/server/licensePlan.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			LicensePlan licensePlan = JSONUtils.fromString(content, LicensePlan.class);

			if (licensePlan != null) {
				licensePlanRepository.save(licensePlan);
			}
		}
	}

	/**
	 * This function adds default steps for the new licensed probe
	 *
	 * @param user_id
	 * @throws IOException
	 */
	public void addDefaultSteps() throws IOException {
		List<Step> stepsDB = stepRepository.getStepsByFlagValue(true);
		if (stepsDB == null || stepsDB.isEmpty()) {

			File file = new File(getClass().getResource("/server/steps.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			Step[] stepArray = JSONUtils.fromString(content, Step[].class);

			if (stepArray != null) {
				List<Step> steps = Arrays.asList(stepArray);
				for (Step step : steps) {
					List<Step> stepsDBsize = stepRepository.getStepsByFlagValue(true);
					if (stepsDBsize == null || stepsDBsize.isEmpty()) {
						step.setNumber(1);
					} else {
						step.setNumber(stepsDBsize.size() + 1);
					}
					step.setActive(1);
					stepRepository.save(step);
				}
			}
		}
	}

	/**
	 * This function initializes keycloak on the startup
	 *
	 * @return
	 */
	public void initializeKeycloak() {
		keycloak = KeycloakBuilder.builder().serverUrl(authServerUrl).grantType("client_credentials").realm(StaticConfigItems.REALM_MASTER)
				.clientSecret(StaticConfigItems.CLIENT_ADMIN_TOKEN).clientId(StaticConfigItems.CLIENT_ADMIN)
				.resteasyClient(new ResteasyClientBuilder().disableTrustManager().connectionPoolSize(10).build()).build();
	}

	/**
	 * This method fetches all predefined conditions and actions for the rule engines and saves their metadata into the database.
	 *
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void initializeRuleEngine() throws ClassNotFoundException, IOException {
		ruleUtils.loadAndSaveTemplateConditions();
		ruleUtils.loadAndSaveTemplateActions();
	}
}
