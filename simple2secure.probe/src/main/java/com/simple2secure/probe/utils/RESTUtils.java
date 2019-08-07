package com.simple2secure.probe.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.simple2secure.probe.config.ProbeConfiguration;

public class RESTUtils {

	private final static Logger log = LoggerFactory.getLogger(RESTUtils.class);

	final static String BEARER = "Bearer ";

	public static boolean checkLicense(String licenseId, String groupId, String probeId) {
		String requestUrl = "";
		try {
			HttpRequestWithBody request = Unirest
					.post(ProbeConfiguration.getInstance().getLoadedConfigItems().getLicenseAPI() + "/{licenseId}/{groupId}/{probeId}");
			request.header("Content-Type", "application/json").header("Authorization", BEARER + ProbeConfiguration.authKey)
					.header("Accept-Language", "en");
			request.routeParam("licenseId", licenseId).routeParam("groupId", groupId).routeParam("probeId", probeId);
			requestUrl = request.getUrl();
			HttpResponse<JsonNode> response = request.asJson();

			if (response.getBody().toString().equalsIgnoreCase("true")) {
				return true;
			}

		} catch (IllegalArgumentException | UnirestException e) {
			log.error("Couldn't check license {} on portal. Reason {}", requestUrl, e.getCause());
			e.printStackTrace();
		}

		return false;
	}
}
