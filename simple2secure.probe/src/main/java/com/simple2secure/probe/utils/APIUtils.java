package com.simple2secure.probe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.config.ProbeConfiguration;

public class APIUtils {

	private final static Logger log = LoggerFactory.getLogger(APIUtils.class);

	final static String BEARER = "Bearer ";

	public static void sendPost(String url_, Object obj) {
		try {
			URL url = new URL(url_);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", BEARER + ProbeConfiguration.authKey);
			conn.setRequestProperty("Accept-Language", "en");

			JSONObject jsonObject = new JSONObject(obj);

			String objJson = jsonObject.toString();

			OutputStream os = conn.getOutputStream();
			os.write(objJson.getBytes());
			os.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send POST to URL {} because of execption {}", url_, e.getStackTrace());
			return;
		} catch (IOException e) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send POST to URL {} because of execption {}", url_, e.getStackTrace());
			return;
		}

		ProbeConfiguration.setAPIAvailablitity(true);

	}

	public static String sendPostWithResponse(String url_, Object obj) {
		try {
			URL url = new URL(url_);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", BEARER + ProbeConfiguration.authKey);
			conn.setRequestProperty("Accept-Language", "en");

			JSONObject jsonObject = new JSONObject(obj);

			String objJson = jsonObject.toString();

			OutputStream os = conn.getOutputStream();
			os.write(objJson.getBytes());
			os.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String response = new String();
			for (String line; (line = br.readLine()) != null; response += line) {
				;
			}

			conn.disconnect();
			ProbeConfiguration.setAPIAvailablitity(true);
			return response;

		} catch (MalformedURLException e) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send POST to URL {} with response because of execption {}", url_, e.getStackTrace());
			return null;
		} catch (IOException e) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send POST to URL {} with response because of execption {}", url_, e.getStackTrace());
			return null;
		}
	}

	public static String sendGet(String url_) {
		String output = "";
		StringBuilder outputBuilder = new StringBuilder();
		try {
			URL url = new URL(url_);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept-Language", "en");
			conn.setRequestProperty("Authorization", BEARER + ProbeConfiguration.authKey);

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), Charset.forName("UTF-8")));

			while ((output = br.readLine()) != null) {
				outputBuilder.append(output);
			}

			if (log.isDebugEnabled()) {
				log.debug("Obtained response from GET. Response {}", outputBuilder.toString());
			}

			conn.disconnect();

		} catch (MalformedURLException e) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send GET to URL {} with response because of execption {}", url_, e.getStackTrace());
			return outputBuilder.toString();
		} catch (IOException e) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send GET to URL {} with response because of execption {}", url_, e.getStackTrace());
			return outputBuilder.toString();
		}
		ProbeConfiguration.setAPIAvailablitity(true);
		return outputBuilder.toString();

	}

	public static int sendLoginPost(String url_, Object obj) throws IOException {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(url_);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			JSONObject jsonObject = new JSONObject(obj);

			String objJson = jsonObject.toString();

			OutputStream os = conn.getOutputStream();
			os.write(objJson.getBytes());
			os.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			ProbeConfiguration.authKey = conn.getHeaderField("Authorization");

			conn.disconnect();

		} catch (MalformedURLException e) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send LOGIN to URL {} with response because of execption {}", url_, e.getStackTrace());
			return conn.getResponseCode();
		} catch (IOException e) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send LOGIN to URL {} with response because of execption {}", url_, e.getStackTrace());
			return conn.getResponseCode();
		}
		ProbeConfiguration.setAPIAvailablitity(true);
		return conn.getResponseCode();

	}

	public static JSONObject decodeJWT() {
		String[] split_string = ProbeConfiguration.authKey.split("\\.");
		String base64EncodedBody = split_string[1];

		Base64 base64Url = new Base64(true);
		String body = new String(base64Url.decode(base64EncodedBody));

		JSONObject bodyJWT = new JSONObject(body);
		return bodyJWT;
	}

}
