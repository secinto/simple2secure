package com.simple2secure.probe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.simple2secure.api.model.Config;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.api.model.Step;

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
		Gson gson = new Gson();
		Config configObj = gson.fromJson(config, Config.class);
		return configObj;
	}

	/**
	 * This function reads runQueries from the file
	 *
	 * @param file
	 * @return
	 */
	public static List<QueryRun> readRunQueriesFromFile(File file) {
		Gson gson = new Gson();

		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			log.error("Provided file not found: " + file.getAbsolutePath());
		}
		JsonReader reader = new JsonReader(fr);
		QueryRun[] queryRunArr = gson.fromJson(reader, QueryRun[].class);
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
		Gson gson = new Gson();

		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			log.error("Provided file not found: " + file.getAbsolutePath());
		}
		JsonReader reader = new JsonReader(fr);
		Processor[] processorArr = gson.fromJson(reader, Processor[].class);
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
		Gson gson = new Gson();

		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			log.error("Provided file not found: " + file.getAbsolutePath());
		}
		JsonReader reader = new JsonReader(fr);
		Step[] stepArr = gson.fromJson(reader, Step[].class);
		List<Step> steps = Arrays.asList(stepArr);
		return steps;
	}
}
