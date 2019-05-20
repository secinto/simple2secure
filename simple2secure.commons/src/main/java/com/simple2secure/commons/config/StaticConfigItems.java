package com.simple2secure.commons.config;

import java.nio.charset.Charset;

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
	public static final String OSQUERY_PATH = ".\\\\\\\\bin\\\\\\\\osquery\\\\\\\\os_win7";

	public static final String[] SECINTO_EMAIL_LIST = { "emir.sahinovic@secinto.com", "emirsahinovic24@gmail.com" };
	public static final String STANDARD_GROUP_NAME = "Standard";
	public static final String DEFAULT_LICENSE_PLAN = "Default";

	public static final String PROFILE_DEPLOYMENT = "deployment";
	public static final String PROFILE_PRODUCTION = "prod";
	public static final String PROFILE_DEFAULT = "default";
	public static final String PROFILE_TEST = "test";

}
