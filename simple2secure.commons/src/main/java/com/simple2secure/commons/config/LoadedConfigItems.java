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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.io.Resources;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadedConfigItems {

	private static Logger log = LoggerFactory.getLogger(LoadedConfigItems.class);
	private String baseProtocol = "https";
	private String baseHost = "localhost";
	private String basePort = "8443";
	private String basePortWeb = "9000";

	private String version = "0.2.1";

	private String[] trustedCertificates = new String[0];

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
		return getBaseURL() + StaticConfigItems.USER_API;
	}

	public String getLoginAPI() {
		return getBaseURL() + StaticConfigItems.LOGIN_API;
	}

	public String getReportsAPI() {
		return getBaseURL() + StaticConfigItems.REPORT_API;
	}

	public String getQueryAPI() {
		return getBaseURL() + StaticConfigItems.QUERY_API;
	}

	public String getDeviceAPI() {
		return getBaseURL() + StaticConfigItems.DEVICE_API;
	}

	public String getStepAPI() {
		return getBaseURL() + StaticConfigItems.STEP_API;
	}

	public String getProcessorAPI() {
		return getBaseURL() + StaticConfigItems.PROCESSOR_API;
	}

	public String getLicenseAPI() {
		return getBaseURL() + StaticConfigItems.LICENSE_API;
	}

	public String getServiceAPI() {
		return getBaseURL() + StaticConfigItems.SERVICE_API;
	}

	public String getGroupAPI() {
		return getBaseURL() + StaticConfigItems.GROUP_API;
	}

	public String getVersion() {
		return version;
	}

	public String[] getTrustedCertificates() {
		return trustedCertificates;
	}

	public void setTrustedCertificates(String[] trustedCertificates) {
		this.trustedCertificates = trustedCertificates;
	}

}
