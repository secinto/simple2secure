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
package com.simple2secure.probe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple2secure.api.model.Config;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.api.model.Step;
import com.simple2secure.commons.json.JSONUtils;

/***
 * Different Utility methods for parsing JSON Strings. Uses the ever-obsolete w3c library, might want to replace it with something newer.
 *
 * @author jhoffmann
 *
 */
public class JsonUtils {
	private static Logger log = LoggerFactory.getLogger(JsonUtils.class);

	/**
	 * This function reads JSON from the URL.
	 *
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String readJsonFromUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}

			return buffer.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Reads the JSON config file provided as file name String and converts the content
	 *
	 * @param filename
	 * @return
	 */
	public static Config readConfigFromFilename(String filename) {
		File configFile = new File(filename);
		if (configFile != null && configFile.exists() && configFile.isFile()) {
			return readConfigFromFile(configFile);
		} else {
			log.error("Couldn't create File object from filename {}", filename);
			return null;
		}
	}

	/**
	 * This file reads local configuration from the file
	 *
	 * @param file
	 * @return
	 */
	public static Config readConfigFromFile(File file) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Config config = mapper.readValue(file, Config.class);
			return config;
		} catch (Exception e) {
			log.error("Couldn't parse JSON to config object. Reason {}", e.getMessage());
		}
		return null;

	}

	public static Config readConfigFromString(String config) {
		Config configObj = JSONUtils.fromString(config, Config.class);
		return configObj;
	}

	/**
	 * This function reads runQueries from the file
	 *
	 * @param file
	 * @return
	 */
	public static List<QueryRun> readRunQueriesFromFile(File file) {

		QueryRun[] queryRunArr = JSONUtils.fromFile(file, QueryRun[].class);
		List<QueryRun> queryRunList = Arrays.asList(queryRunArr);
		return queryRunList;

	}

	/**
	 * This function reads processors from the file
	 *
	 * @param file
	 * @return
	 */
	public static List<Processor> readProcessorsFromFile(File file) {
		Processor[] processorArr = JSONUtils.fromFile(file, Processor[].class);
		List<Processor> processors = Arrays.asList(processorArr);
		return processors;

	}

	/**
	 * This function reads steps from the file
	 *
	 * @param file
	 * @return
	 */
	public static List<Step> readStepsFromFile(File file) {
		Step[] stepArr = JSONUtils.fromFile(file, Step[].class);
		List<Step> steps = Arrays.asList(stepArr);
		return steps;
	}
}
