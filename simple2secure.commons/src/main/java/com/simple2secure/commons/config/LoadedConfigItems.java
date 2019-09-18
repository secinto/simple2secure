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
package com.simple2secure.commons.config;

import java.nio.charset.Charset;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.io.Resources;

public class LoadedConfigItems {

	private static Logger log = LoggerFactory.getLogger(LoadedConfigItems.class);
	private String baseProtocol = "https";
	private String baseHost = "localhost";
	private String basePort = "8443";
	private String basePortWeb = "9000";

	private String reportURL = "/config/reports.json";
	private String stepsURL = "/config/steps.json";
	private String configURL = "/config/config.json";
	private String processorsURL = "/config/processors.json";
	private String queryURL = "/config/queries.json";
	private String toolsURL = "/config/tools.json";
	private String testsURL = "/config/tests.json";
	private String groupURL = "/config/group.json";
	private String settingsURL = "/config/settings.json";
	private String licensePlanURL = "/config/licensePlan.json";

	private String usersAPI = "/api/users";
	private String loginAPI = "/api/login";
	private String reportsAPI = "/api/reports";
	private String queryAPI = "/api/config/query";
	private String configAPI = "/api/config";
	private String deviceAPI = "/api/device";
	private String packetAPI = "/api/packet";
	private String stepAPI = "/api/steps";
	private String processorAPI = "/api/processors";
	private String licenseAPI = "/api/license";
	private String serviceAPI = "/api/service";

	private static LoadedConfigItems instance;

	public static LoadedConfigItems getInstance() {
		if (instance == null) {
			instance = new LoadedConfigItems();
			instance.init();
		}
		return instance;
	}

	public LoadedConfigItems() {

	}

	protected void init() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		try {
			/*
			 * Read configuration from resources application configuration
			 */
			LoadedConfigItems user = mapper.readValue(Resources.toString(Resources.getResource("config.items.yml"), Charset.forName("UTF-8")),
					LoadedConfigItems.class);

			log.debug(ReflectionToStringBuilder.toString(user, ToStringStyle.MULTI_LINE_STYLE));

		} catch (Exception e) {
			log.error("Couldn't load loadable configuration items. Cause {}, Reason {}", e.getCause(), e.getStackTrace());
		}
	}

	public String getBaseProtocol() {
		return baseProtocol;
	}

	public void setBaseProtocol(String baseProtocol) {
		this.baseProtocol = baseProtocol;
	}

	public String getBaseHost() {
		return baseHost;
	}

	public void setBaseHost(String baseHost) {
		this.baseHost = baseHost;
	}

	public String getBasePort() {
		return basePort;
	}

	public void setBasePort(String basePort) {
		this.basePort = basePort;
	}

	public String getBasePortWeb() {
		return basePortWeb;
	}

	public void setBasePortWeb(String basePortWeb) {
		this.basePortWeb = basePortWeb;
	}

	public String getBaseURL() {
		return baseProtocol + "://" + baseHost + ":" + basePort;
	}

	public String getBaseURLWeb() {
		return baseProtocol + "://" + baseHost + ":" + basePortWeb;
	}

	public String getReportURL() {
		return getBaseURL() + reportURL;
	}

	public String getStepsURL() {
		return getBaseURL() + stepsURL;
	}

	public String getConfigURL() {
		return getBaseURL() + configURL;
	}

	public String getProcessorsURL() {
		return getBaseURL() + processorsURL;
	}

	public String getQueryURL() {
		return getBaseURL() + queryURL;
	}

	public String getToolsURL() {
		return getBaseURL() + toolsURL;
	}

	public String getTestsURL() {
		return getBaseURL() + testsURL;
	}

	public String getGroupURL() {
		return getBaseURL() + groupURL;
	}

	public String getSettingsURL() {
		return getBaseURL() + settingsURL;
	}

	public String getUsersAPI() {
		return getBaseURL() + usersAPI;
	}

	public String getLoginAPI() {
		return getBaseURL() + loginAPI;
	}

	public String getReportsAPI() {
		return getBaseURL() + reportsAPI;
	}

	public String getQueryAPI() {
		return getBaseURL() + queryAPI;
	}

	public String getConfigAPI() {
		return getBaseURL() + configAPI;
	}

	public String getDeviceAPI() {
		return getBaseURL() + deviceAPI;
	}

	public String getPacketAPI() {
		return getBaseURL() + packetAPI;
	}

	public String getStepAPI() {
		return getBaseURL() + stepAPI;
	}

	public String getProcessorAPI() {
		return getBaseURL() + processorAPI;
	}

	public String getLicenseAPI() {
		return getBaseURL() + licenseAPI;
	}

	public String getServiceAPI() {
		return getBaseURL() + serviceAPI;
	}

	public String getLicensePlanURL() {
		return getBaseURL() + licensePlanURL;
	}
}