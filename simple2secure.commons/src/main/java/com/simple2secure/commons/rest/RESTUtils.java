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
package com.simple2secure.commons.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.commons.json.JSONUtils;

public class RESTUtils {

	private final static Logger log = LoggerFactory.getLogger(RESTUtils.class);

	final static String BEARER = "Bearer ";

	public static String sendPost(String url, Object obj) {
		return sendPost(url, obj, null);
	}

	public static String sendPost(String url, Object obj, String authKey) {
		try {

			HttpsURLConnection connection = getConnection(url, "POST", authKey);

			OutputStream os = connection.getOutputStream();
			os.write(JSONUtils.toString(obj).getBytes());
			os.flush();

			return getResponse(connection);

		} catch (MalformedURLException e) {
			log.error("Couldn 't send POST to URL {} with response because of malformed URL {}", url, e.getStackTrace());
			return null;
		} catch (IOException e) {
			log.error("Couldn 't send POST to URL {} with response because of execption {}", url, e.getStackTrace());
			return null;
		}
	}

	public static String sendGet(String url) {
		return sendGet(url, null);
	}

	public static String sendGet(String url, String authKey) {
		try {

			HttpURLConnection connection = getConnection(url, "GET", authKey);
			return getResponse(connection);
		} catch (MalformedURLException e) {
			log.error("Couldn 't send GET to URL {} with response because of malformed URL {}", url, e.getStackTrace());
		} catch (IOException e) {
			log.error("Couldn 't send GET to URL {} with response because of execption {}", url, e.getStackTrace());
		}
		return null;
	}

	private static String getResponse(HttpURLConnection connection) throws IOException {
		String output = null;
		StringBuilder outputBuilder = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

		while ((output = br.readLine()) != null) {
			outputBuilder.append(output);
		}

		if (log.isDebugEnabled()) {
			log.debug("Obtained response from GET. Response {}", outputBuilder.toString());
		}

		connection.disconnect();

		return outputBuilder.toString();
	}

	private static HttpsURLConnection getConnection(String url, String method, String authKey) throws IOException {
		URL remote = new URL(url);

		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(final String hostname, final SSLSession session) {
				return true;
			}
		});

		HttpsURLConnection conn = (HttpsURLConnection) remote.openConnection();

		conn.setDoOutput(true);
		conn.setRequestMethod(method);
		conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		conn.setRequestProperty("Accept-Language", "en");
		if (!Strings.isNullOrEmpty(authKey)) {
			conn.setRequestProperty("Authorization", BEARER + authKey);
		}

		return conn;
	}

	/**
	 *
	 * @param string_url
	 * @return
	 */
	public static boolean netIsAvailable(String string_url) {
		try {
			final URL url = new URL(string_url);

			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

				@Override
				public boolean verify(final String hostname, final SSLSession session) {
					return true;
				}
			});

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.connect();
			return true;
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
			return false;
		} catch (IOException e) {
			return false;
		}
	}

}