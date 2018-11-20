package com.simple2secure.commons.json;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {
	private static Logger log = LoggerFactory.getLogger(JSONUtils.class);

	private static ObjectMapper mapper = new ObjectMapper();

	public static String fromJSON(JsonNode content) {

		return null;
	}

	public static JsonNode fromString(String content) {
		try {
			return mapper.readTree(content);
		} catch (IOException e) {
			log.error("Couldn't map string to JsonNode. Reason {}", e);
		}
		return null;
	}

	public static <T> T fromString(String content, Class<T> valueType) {
		try {
			return (T) mapper.readValue(content, valueType);
		} catch (Exception e) {
			log.error("Couldn't map string to Class {}. Reason {}", valueType, e);
		}
		return null;
	}

	public static <T> T fromFile(File content, Class<T> valueType) {
		try {
			return (T) mapper.readValue(content, valueType);
		} catch (Exception e) {
			log.error("Couldn't map string to Class {}. Reason {}", valueType, e);
		}
		return null;
	}

	public static String toString(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Couldn't map Object to String. Reason {}", e);
		}
		return null;
	}
}
