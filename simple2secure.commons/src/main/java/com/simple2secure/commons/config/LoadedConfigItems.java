/**
 *********************************************************************
 *
 * Copyright (C) 2019 by secinto GmbH (http://www.secinto.com)
 *
 *********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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
	private String baseHost = "https://localhost";
	private String basePort = "8443";
	private String basePortWeb = "9000";

	private String version = "0.2.0";

	private String usersAPI = "/api/user";
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
	private String groupAPI = "/api/group";
	private String probeAPI = "/api/probe";

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

			if (user != null) {
				log.debug(ReflectionToStringBuilder.toString(user, ToStringStyle.MULTI_LINE_STYLE));
				instance = user;
			}

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

	public String getGroupAPI() {
		return getBaseURL() + groupAPI;
	}

	public String getProbeAPI() {
		return getBaseURL() + probeAPI;
	}

	public String getVersion() {
		return version;
	}
}
