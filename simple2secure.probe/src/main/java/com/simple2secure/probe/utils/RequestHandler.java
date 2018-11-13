package com.simple2secure.probe.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.simple2secure.probe.config.ProbeConfiguration;

public class RequestHandler {

	private final static Logger log = LoggerFactory.getLogger(RequestHandler.class);

	final static String BEARER = "Bearer ";
	private static String output;

	public static void sendPost(String url_, Object obj) {
		String response;
		try {
			response = createPostRequest(url_, obj);
			log.debug("Response from POST reques: " + response);
		}catch(UnirestException ex) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send POST to URL {} because of execption {}", url_, ex.getStackTrace());
		}
		ProbeConfiguration.setAPIAvailablitity(true);

	}
	
	public static String sendPostReceiveResponse(String url_, Object obj) {
		String response = new String();
		try {
			response = createPostRequest(url_, obj);
			log.debug("Response from POST request: " + response);
		}catch(UnirestException ex) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send POST to URL {} because of execption {}", url_, ex.getStackTrace());
		}
		ProbeConfiguration.setAPIAvailablitity(true);
		return response;
	}

	public static String sendGet(String url_) {
		String response = new String();
		try {
			response = createGetRequest(url_);
		} catch (UnirestException ex) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send GET to URL {} with response because of execption {}", url_, ex.getStackTrace());
		}
		ProbeConfiguration.setAPIAvailablitity(true);
		return response;

	}

	private static String createPostRequest(String url, Object object) throws UnirestException {
		ObjectMapper mapper = new ObjectMapper();
		HttpResponse<JsonNode> jsonResponse = Unirest.post(url).header("Content-Type", "application/json").
				header("Authorization", BEARER + ProbeConfiguration.authKey).header("Accept-Language", "en").body(object).asJson();
		return jsonResponse.toString();
	}
	
	private static String createGetRequest(String url) throws UnirestException {
		ObjectMapper mapper = new ObjectMapper();
		HttpResponse<JsonNode> jsonResponse = Unirest.get(url).header("Content-Type", "application/json").
				header("Authorization", BEARER + ProbeConfiguration.authKey).header("Accept-Language", "en").asJson();
		return jsonResponse.toString();
	}
}
