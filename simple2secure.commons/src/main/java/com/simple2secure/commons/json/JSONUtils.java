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
	 * Creates an object of the specified valueType from the provided JSON String content provided as input. If mapping the {@link String} to
	 * the object of type valueType is not successful <code>null</code> is returned.
	 *
	 * @param content
	 *          The JSON String representation as {@link String}
	 * @param valueType
	 *          The type of object to which the content should be mapped.
	 * @return The created object of type valueType if successful, otherwise null.
	 */
	public static <T> T fromString(String content, Class<T> valueType) {
		try {
			return mapper.readValue(content, valueType);
		} catch (Exception e) {
			log.error("Couldn't map string to Class {}. Reason {}", valueType, e);
		}
		return null;
	}

	/**
	 * Creates an object of the specified valueType from the File content provided as input. If mapping the {@link File} to the object of type
	 * valueType is not successful <code>null</code> is returned.
	 *
	 * @param content
	 *          The JSON String representation as {@link File}
	 * @param valueType
	 *          The type of object to which the content should be mapped.
	 * @return The created object of type valueType if successful, otherwise null.
	 */
	public static <T> T fromFile(File content, Class<T> valueType) {
		try {
			return mapper.readValue(content, valueType);
		} catch (Exception e) {
			log.error("Couldn't map string to Class {}. Reason {}", valueType, e);
		}
		return null;
	}

	/**
	 * Parses the provided Object and returns a JSON String representation of it.
	 *
	 * @param object
	 *          The object which should be mapped a JSON String representation.
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
