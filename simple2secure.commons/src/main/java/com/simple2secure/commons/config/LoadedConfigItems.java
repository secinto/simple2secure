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

	private String baseURL = baseProtocol + "://" + baseHost + ":" + basePort;
	private String baseURLWeb = baseProtocol + "://" + baseHost + ":" + basePortWeb;

	private String baseKubernetesURL = "https://35.232.109.156";

	private String reportURL = baseURL + "/config/reports.json";
	private String stepsURL = baseURL + "/config/steps.json";
	private String configURL = baseURL + "/config/config.json";
	private String endpointsURL = baseURL + "/config/endpoints.json";
	private String processorsURL = baseURL + "/config/processors.json";
	private String queryURL = baseURL + "/config/queries.json";
	private String toolsURL = baseURL + "/config/tools.json";
	private String testsURL = baseURL + "/config/tests.json";
	private String groupURL = baseURL + "/config/group.json";
	private String settingsURL = baseURL + "/config/settings.json";

	private String usersAPI = baseURL + "/api/users";
	private String endpointsAPI = baseURL + "/api/endpoints";
	private String loginAPI = baseURL + "/api/login";
	private String reportsAPI = baseURL + "/api/reports";
	private String queryAPI = baseURL + "/api/config/query";
	private String configAPI = baseURL + "/api/config";
	private String deviceAPI = baseURL + "/api/device";
	private String packetAPI = baseURL + "/api/packet";
	private String stepAPI = baseURL + "/api/steps";
	private String processorAPI = baseURL + "/api/processors";
	private String licenseAPI = baseURL + "/api/license";

	public LoadedConfigItems() {
	}

	public void init() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		try {
			/*
			 * Read configuration from resources application configuration
			 */
			LoadedConfigItems user = mapper.readValue(
					Resources.toString(Resources.getResource("config.items.yml"), Charset.forName("UTF-8")),
					LoadedConfigItems.class);

			log.debug(ReflectionToStringBuilder.toString(user, ToStringStyle.MULTI_LINE_STYLE));

		} catch (Exception e) {
			log.error("Couldn't load loadable configuration items. Cause {}, Reason {}", e.getCause(),
					e.getStackTrace());
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

	public String getBaseKubernetesURL() {
		return baseKubernetesURL;
	}

	public void setBaseKubernetesURL(String baseKubernetesURL) {
		this.baseKubernetesURL = baseKubernetesURL;
	}

	public String getReportURL() {
		return reportURL;
	}

	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}

	public String getStepsURL() {
		return stepsURL;
	}

	public void setStepsURL(String stepsURL) {
		this.stepsURL = stepsURL;
	}

	public String getConfigURL() {
		return configURL;
	}

	public void setConfigURL(String configURL) {
		this.configURL = configURL;
	}

	public String getEndpointsURL() {
		return endpointsURL;
	}

	public void setEndpointsURL(String endpointsURL) {
		this.endpointsURL = endpointsURL;
	}

	public String getProcessorsURL() {
		return processorsURL;
	}

	public void setProcessorsURL(String processorsURL) {
		this.processorsURL = processorsURL;
	}

	public String getQueryURL() {
		return queryURL;
	}

	public void setQueryURL(String queryURL) {
		this.queryURL = queryURL;
	}

	public String getToolsURL() {
		return toolsURL;
	}

	public void setToolsURL(String toolsURL) {
		this.toolsURL = toolsURL;
	}

	public String getTestsURL() {
		return testsURL;
	}

	public void setTestsURL(String testsURL) {
		this.testsURL = testsURL;
	}

	public String getGroupURL() {
		return groupURL;
	}

	public void setGroupURL(String groupURL) {
		this.groupURL = groupURL;
	}

	public String getSettingsURL() {
		return settingsURL;
	}

	public void setSettingsURL(String settingsURL) {
		this.settingsURL = settingsURL;
	}

	public String getUsersAPI() {
		return usersAPI;
	}

	public void setUsersAPI(String usersAPI) {
		this.usersAPI = usersAPI;
	}

	public String getEndpointsAPI() {
		return endpointsAPI;
	}

	public void setEndpointsAPI(String endpointsAPI) {
		this.endpointsAPI = endpointsAPI;
	}

	public String getLoginAPI() {
		return loginAPI;
	}

	public void setLoginAPI(String loginAPI) {
		this.loginAPI = loginAPI;
	}

	public String getReportsAPI() {
		return reportsAPI;
	}

	public void setReportsAPI(String reportsAPI) {
		this.reportsAPI = reportsAPI;
	}

	public String getQueryAPI() {
		return queryAPI;
	}

	public void setQueryAPI(String queryAPI) {
		this.queryAPI = queryAPI;
	}

	public String getConfigAPI() {
		return configAPI;
	}

	public void setConfigAPI(String configAPI) {
		this.configAPI = configAPI;
	}

	public String getDeviceAPI() {
		return deviceAPI;
	}

	public void setDeviceAPI(String deviceAPI) {
		this.deviceAPI = deviceAPI;
	}

	public String getPacketAPI() {
		return packetAPI;
	}

	public void setPacketAPI(String packetAPI) {
		this.packetAPI = packetAPI;
	}

	public String getStepAPI() {
		return stepAPI;
	}

	public void setStepAPI(String stepAPI) {
		this.stepAPI = stepAPI;
	}

	public String getProcessorAPI() {
		return processorAPI;
	}

	public void setProcessorAPI(String processorAPI) {
		this.processorAPI = processorAPI;
	}

	public String getLicenseAPI() {
		return licenseAPI;
	}

	public void setLicenseAPI(String licenseAPI) {
		this.licenseAPI = licenseAPI;
	}

}
