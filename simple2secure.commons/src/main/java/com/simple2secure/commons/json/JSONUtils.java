/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
