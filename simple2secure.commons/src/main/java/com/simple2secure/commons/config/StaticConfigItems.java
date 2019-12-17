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

import com.simple2secure.api.model.LocaleLanguage;

public class StaticConfigItems {
	public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
	public static final Charset UTF_8 = Charset.forName("UTF-8");

	public static int username_exists = 1;
	public static int email_exists = 2;
	public static int user_created = 3;
	public static int user_creation_db_error = 4;

	public static String resource_location = "classpath:/server/";

	public static String email_subject_al = "simple2secure Activation Link";
	public static String email_subject_pr = "simple2secure Reset Password Link";
	public static String email_subjct_pcs = "simple2secure Password Change Successful";
	public static String email_subject_inv = "simple2secure Context Invitation";
	public static long expiration_time_password_reset = 1200000; // 20 minutes

	public static String type_password_update = "password_update";
	public static String type_email = "email";
	public static String type_update_user_info = "update_user_info";
	public static String type_add_by_user = "add_by_user";

	/* Probe config items */
	public final static String PROBE_TITLE = "simple2secure Probe";
	public static final int SNAPLEN = 65536; // [bytes]
	public static final int READ_TIMEOUT = 10; // [ms]

	public static final long tokenValidity = 24 * 60 * 60 * 100;

	public static final String DEFAULT_PROCESSOR = "default";
	public static final String CONFIG_JSON_LOCATION = "/configuration/config.json";
	public static final String QUERIES_JSON_LOCATION = "/configuration/queries.json";
	public static final String PROCESSORS_JSON_LOCATION = "/configuration/processors.json";
	public static final String STEPS_JSON_LOCATION = "/configuration/steps.json";
	public static final String KEYS_LOCATION = "./src/main/resources/keys/";
	public static final String[] OSQUERY_DATA_LOCALTION = { "/osquery/os_win7/osquery.conf", "/osquery/os_win7/osqueryi.exe" };

	public static final String[] SECINTO_EMAIL_LIST = { "s2s.test@secinto.at" };
	public static final String STANDARD_GROUP_NAME = "Standard";
	public static final String DEFAULT_LICENSE_PLAN = "Default";

	public static final String PROFILE_DEPLOYMENT = "deployment";
	public static final String PROFILE_PRODUCTION = "prod";
	public static final String PROFILE_DEFAULT = "default";
	public static final String PROFILE_TEST = "test";
	public static final int DEFAULT_VALUE_SIZE = 10;
	public static final int MAX_VALUE_SIZE = 10;
	public static final LocaleLanguage DEFAULT_LOCALE = LocaleLanguage.ENGLISH;

	public static final String API_PREFIX = "/api";

	public static final String API_VERSION = "/v1";

	public static final String API_ENDPOINT = API_PREFIX + API_VERSION;

	public static final String CONTEXT_API = API_ENDPOINT + "/context";
	public static final String DASH_API = API_ENDPOINT + "/dash";
	public static final String DEVICE_API = API_ENDPOINT + "/devices";
	public static final String DOWNLOAD_API = API_ENDPOINT + "/download";
	public static final String EMAIL_API = API_ENDPOINT + "/email";
	public static final String GROUP_API = API_ENDPOINT + "/group";
	public static final String LICENSE_API = API_ENDPOINT + "/license";
	public static final String NOTIFICATION_API = API_ENDPOINT + "/notification";
	public static final String PROCESSOR_API = API_ENDPOINT + "/processors";
	public static final String QUERY_API = API_ENDPOINT + "/query";
	public static final String REPORT_API = API_ENDPOINT + "/reports";
	public static final String RULE_API = API_ENDPOINT + "/rule";
	public static final String SEARCH_API = API_ENDPOINT + "/search";
	public static final String SERVICE_API = API_ENDPOINT + "/service";
	public static final String SETTINGS_API = API_ENDPOINT + "/settings";
	public static final String STEP_API = API_ENDPOINT + "/steps";
	public static final String SUT_API = API_ENDPOINT + "/sut";
	public static final String TEST_API = API_ENDPOINT + "/test";
	public static final String SEQUENCE_API = API_ENDPOINT + "/sequence";
	public static final String USER_API = API_ENDPOINT + "/user";
	public static final String WIDGET_API = API_ENDPOINT + "/widget";
	public static final String ERROR_API = API_ENDPOINT + "/error";
	public static final String LOGIN_API = API_ENDPOINT + "/login";

	public static final String CONTEXT_ANNOTATION_TAG = "/{contextId}";
	public static final String DESTGROUP_ANNOTATION_TAG = "/{destGroupId}";
	public static final String DEVICE_ANNOTATION_TAG = "/{deviceId}";
	public static final String EMAILCONFIG_ANNOTATION_TAG = "/{emailConfigId}";
	public static final String GROUP_ANNOTATION_TAG = "/{groupId}";
	public static final String HOSTNAME_ANNOTATION_TAG = "/{hostname}";
	public static final String LICENSEPLAN_ANNOTATION_TAG = "/{licensePlanId}";
	public static final String PAGE_ANNOTATION_TAG = "/{page}";
	public static final String PROCESSOR_ANNOTATION_TAG = "/{processorId}";
	public static final String QUERY_ANNOTATION_TAG = "/{queryId}";
	public static final String REPORT_ANNOTATION_TAG = "/{reportId}";
	public static final String RULE_ANNOTATION_TAG = "/{ruleId}";
	public static final String SEARCH_ANNOTATION_TAG = "/{searchQuery}";
	public static final String SIZE_ANNOTATION_TAG = "/{size}";
	public static final String STEP_ANNOTATION_TAG = "/{stepId}";
	public static final String SUT_ANNOTATION_TAG = "/{sutId}";
	public static final String MACRO_ANNOTATION_TAG = "/{testMacroId}";
	public static final String USER_ANNOTATION_TAG = "/{userId}";
	public static final String VERSION_ANNOTATION_TAG = "/{version}";

}
