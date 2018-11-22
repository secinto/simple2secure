package com.simple2secure.commons.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.json.JSONUtils;

public class RESTUtils {

	private final static Logger log = LoggerFactory.getLogger(RESTUtils.class);

	final static String BEARER = "Bearer ";

	public static String sendPost(String url, Object obj) {
		try {

			HttpURLConnection connection = getConnection(url, "GET");

			OutputStream os = connection.getOutputStream();
			os.write(JSONUtils.toString(obj).getBytes());
			os.flush();

			return getResponse(connection);

		} catch (MalformedURLException e) {
			log.error("Couldn 't send POST to URL {} with response because of execption {}", url, e.getStackTrace());
			return null;
		} catch (IOException e) {
			log.error("Couldn 't send POST to URL {} with response because of execption {}", url, e.getStackTrace());
			return null;
		}
	}

	public static String sendGet(String url) {
		try {
			HttpURLConnection connection = getConnection(url, "GET");
			return getResponse(connection);
		} catch (MalformedURLException e) {
			log.error("Couldn 't send GET to URL {} because URL is malformed. {}", url, e.getStackTrace());
		} catch (IOException e) {
			log.error("Couldn 't send GET to URL {} with response because of execption {}", url, e.getStackTrace());
		}
		return null;
	}

	private static String getResponse(HttpURLConnection connection) throws IOException {
		String output = null;
		StringBuilder outputBuilder = new StringBuilder();
		BufferedReader br = new BufferedReader(
				new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

		while ((output = br.readLine()) != null) {
			outputBuilder.append(output);
		}

		if (log.isDebugEnabled()) {
			log.debug("Obtained response from GET. Response {}", outputBuilder.toString());
		}

		connection.disconnect();

		return outputBuilder.toString();
	}

	private static HttpURLConnection getConnection(String url, String method) throws IOException {
		URL remote = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) remote.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(method);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept-Language", "en");
		// conn.setRequestProperty("Authorization", BEARER +
		// ProbeConfiguration.authKey);

		return conn;
	}

}