package com.simple2secure.commons.json;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {
	private static Logger log = LoggerFactory.getLogger(JSONUtils.class);

	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * Creates an object of the specified valueType from the provided JSON String
	 * content provided as input. If mapping the {@link String} to the object of
	 * type valueType is not successful <code>null</code> is returned.
	 * 
	 * @param content   The JSON String representation as {@link String}
	 * @param valueType The type of object to which the content should be mapped.
	 * @return The created object of type valueType if successful, otherwise null.
	 */
	public static <T> T fromString(String content, Class<T> valueType) {
		try {
			return (T) mapper.readValue(content, valueType);
		} catch (Exception e) {
			log.error("Couldn't map string to Class {}. Reason {}", valueType, e);
		}
		return null;
	}

	/**
	 * Creates an object of the specified valueType from the File content provided
	 * as input. If mapping the {@link File} to the object of type valueType is not
	 * successful <code>null</code> is returned.
	 * 
	 * @param content   The JSON String representation as {@link File}
	 * @param valueType The type of object to which the content should be mapped.
	 * @return The created object of type valueType if successful, otherwise null.
	 */
	public static <T> T fromFile(File content, Class<T> valueType) {
		try {
			return (T) mapper.readValue(content, valueType);
		} catch (Exception e) {
			log.error("Couldn't map string to Class {}. Reason {}", valueType, e);
		}
		return null;
	}

	/**
	 * Parses the provided Object and returns a JSON String representation of it.
	 * 
	 * @param object The object which should be mapped a JSON String representation.
	 * @return The JSON representation as String
	 */
	public static String toString(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Couldn't map Object to String. Reason {}", e);
		}
		return null;
	}
}
