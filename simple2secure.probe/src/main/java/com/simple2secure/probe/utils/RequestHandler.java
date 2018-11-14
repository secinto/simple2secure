package com.simple2secure.probe.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.simple2secure.probe.config.ProbeConfiguration;

public class RequestHandler {

	private final static Logger log = LoggerFactory.getLogger(RequestHandler.class);
	final static String BEARER = "Bearer ";

	private RequestHandler() {}
	
	public static void sendPost(String url_, Object obj) {
		sendPostReceiveResponse(url_, obj);
	}
	
	public static String sendPostReceiveResponse(String url_, Object obj) {
		ObjectMapper oM = new ObjectMapper();
		String requestBody = new String();
		String response = "";
		
		try {
			requestBody = oM.writeValueAsString(obj);
			HttpResponse<String> jsonResponse = createPostRequest(url_, requestBody);
			response = jsonResponse.getBody();
			log.debug("Response from POST request: " + response);
		}catch(UnirestException | JsonProcessingException ex) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send POST to URL {} because of execption {}", url_, ex);
		}
		ProbeConfiguration.setAPIAvailablitity(true);
		return response;
	}

	public static String sendGet(String url_) {	
		String response = "";
		try {
			HttpResponse<String> jsonResponse = createGetRequest(url_);
			response = jsonResponse.getBody();
			log.debug("Response from POST request: " + response);
			
		} catch (UnirestException ex) {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("Couldn 't send GET to URL {} with response because of execption {}", url_, ex.getStackTrace());
		}
		ProbeConfiguration.setAPIAvailablitity(true);
		return response;
	}

	private static HttpResponse<String> createPostRequest(String url, String object) throws UnirestException {
		HttpResponse<String> jsonResponse = Unirest.post(url).header("Content-Type", "application/json").
				header("Authorization", BEARER + ProbeConfiguration.authKey).header("Accept-Language", "en").body(object).asString();
		return jsonResponse;
	}
	
	private static HttpResponse<String> createGetRequest(String url) throws UnirestException {
		HttpResponse<String> jsonResponse = Unirest.get(url).header("Content-Type", "application/json").
				header("Authorization", BEARER + ProbeConfiguration.authKey).header("Accept-Language", "en").asString();
		return jsonResponse;
	}
}
