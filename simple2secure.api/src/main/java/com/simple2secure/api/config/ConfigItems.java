package com.simple2secure.api.config;

import java.nio.charset.Charset;

public class ConfigItems {
	public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
	public static final Charset UTF_8 = Charset.forName("UTF-8");

	public static String BASE_URL = "https://localhost:8443";
	// public static final String BASE_URL = "http://s2s.secinto.com:8080/s2s";
	public static String BASE_URL_WEB = "http://localhost:9000";
	// public static final String BASE_URL_WEB = "http://s2s.secinto.com:8080/web";
	// public static final String KUBERNETES_BASE_URL =
	// "https://api-simple2secure-k8s-loc-dft6hq-657105971.eu-central-1.elb.amazonaws.com";
	// public static final String KUBERNETES_BASE_URL =
	// "https://144.76.93.104:6443";
	public static String KUBERNETES_BASE_URL = "https://35.232.109.156";

	public static String report_url = BASE_URL + "/config/reports.json";
	public static String steps_url = BASE_URL + "/config/steps.json";
	public static String services_url = BASE_URL + "/config/services.json";
	public static String config_url = BASE_URL + "/config/config.json";
	public static String endpoints_url = BASE_URL + "/config/endpoints.json";
	public static String processors_url = BASE_URL + "/config/processors.json";
	public static String tasks_url = BASE_URL + "/config/tasks.json";
	public static String query_url = BASE_URL + "/config/queries.json";
	public static String tools_url = BASE_URL + "/config/tools.json";
	public static String tests_url = BASE_URL + "/config/tests.json";
	public static String group_url = BASE_URL + "/config/group.json";
	public static String settings_url = BASE_URL + "/config/settings.json";

	public static String users_api = BASE_URL + "/api/users";
	public static String endpoints_api = BASE_URL + "/api/endpoints";
	public static String login_api = BASE_URL + "/api/login";
	public static String reports_api = BASE_URL + "/api/reports";
	public static String system_query_api = BASE_URL + "/api/config/query";
	public static String config_api = BASE_URL + "/api/config";
	public static String device_api = BASE_URL + "/api/device";
	public static String packet_api = BASE_URL + "/api/packet";
	public static String step_api = BASE_URL + "/api/steps";
	public static String processor_api = BASE_URL + "/api/processors";
	public static String license_api = BASE_URL + "/api/license";
	public static int username_exists = 1;
	public static int email_exists = 2;
	public static int user_created = 3;
	public static int user_creation_db_error = 4;

	public static String resource_location = "classpath:/server/";

	public static String email_subject_al = "simple2Secure Activation Link";
	public static String email_subject_pr = "simple2Secure Reset Password Link";
	public static String email_subjct_pcs = "simple2Secure Password Change Successful";
	public static long expiration_time_password_reset = 1200000; // 20 minutes
	// public static String kubernetes_token = "CAA3FDFoxxtB8gkG66reLVS5IMF3YXsi";
	// public static String kubernetes_token =
	// "eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tOWhocWsiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6ImM4ZTRiZjVlLTkxODMtMTFlOC05YTRiLTA4NjA2ZTY5NTNmMSIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.v-5yG-tMf6V0_y4ThzoZaLjXzMvvhbF8IpeAcKPqrBgCKB1Lh55y4PZ5hDXvHph-vTHuoFRvVBo1gUHqIEG7Zi5DwtetrLXJobB3t6IsYzGsBnaX_uv9Y8aRCn0E4uMNH-zb0BSG-HCT_zv0Ocirkzv23GK2uRn9EbVOJpLARBK6R2aiVoUSUuZd1dn1TYrg-SuFZum-aSpKX_bt-Ats2x6As8odz7bGzaPMWyVmmW59fPRYfv42wFtflXHpCuWVI_OLQcw1APD287gL32swfPgqxdMMTSeAr8VBcUtL9vJJLgqG7JkzZqz_taXiqoVG85LWZ0RdzO12kXzpya4WOg";
	public static String kubernetes_token = "ya29.GqMBGwYHzCEj5-DX5MqTT9wgjd1cAdsPm4jjJCmV0P-eR7EVPobaqA8YrF8JP7kdnI51T5EQLW6JsWA0K4zX1J3vLnKKWtWhhT8Jjutk9HjyMJJwwNklEeRBWtk527yLgJD7ldUPjfY_0h6ebIasvuIRwYk9Kd4gazNt0L_QQJbE9MzSKpddNquGVCL4_DCh_fY5TTleNI3ao0iXP54BBSX7wXfciw";
	public static String type_password_update = "password_update";
	public static String type_email = "email";
	public static String type_update_user_info = "update_user_info";
	public static String type_add_by_user = "add_by_user";

	/* Probe config items */
	public final static String PROBE_TITLE = "simple2Secure Probe";
	public static final int SNAPLEN = 65536; // [bytes]
	public static final int READ_TIMEOUT = 10; // [ms]
	public static final String DEFAULT_PROCESSOR = "default";
	public static final String CONFIG_JSON_LOCATION = "/configuration/config.json";
	public static final String QUERIES_JSON_LOCATION = "/configuration/queries.json";
	public static final String PROCESSORS_JSON_LOCATION = "/configuration/processors.json";
	public static final String STEPS_JSON_LOCATION = "/configuration/steps.json";


}
