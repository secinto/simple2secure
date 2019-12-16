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

	public static final String VERSIONING_API = "/api/v1/";

	public static final String CONFIG_API = VERSIONING_API + "config";
	public static final String CONTEXT_API = VERSIONING_API + "context";
	public static final String DASH_API = VERSIONING_API + "dash";
	public static final String DEVICE_API = VERSIONING_API + "device";
	public static final String DOWNLOAD_API = VERSIONING_API + "download";
	public static final String EMAIL_API = VERSIONING_API + "email";
	public static final String GROUP_API = VERSIONING_API + "group";
	public static final String LICENSE_API = VERSIONING_API + "license";
	public static final String NOTIFICATION_API = VERSIONING_API + "notification";
	public static final String PROCESSOR_API = VERSIONING_API + "processors";
	public static final String QUERY_API = VERSIONING_API + "query";
	public static final String REPORT_API = VERSIONING_API + "reports";
	public static final String RULE_API = VERSIONING_API + "rule";
	public static final String SEARCH_API = VERSIONING_API + "search";
	public static final String SERVICE_API = VERSIONING_API + "service";
	public static final String SETTINGS_API = VERSIONING_API + "settings";
	public static final String STEP_API = VERSIONING_API + "steps";
	public static final String SUT_API = VERSIONING_API + "sut";
	public static final String TEST_API = VERSIONING_API + "test";
	public static final String SEQUENCE_API = VERSIONING_API + "sequence";
	public static final String USER_API = VERSIONING_API + "user";
	public static final String WIDGET_API = VERSIONING_API + "widget";
	public static final String ERROR_API = "/api/v1/error";
	public static final String LOGIN_API = "/api/v1/login";

}
