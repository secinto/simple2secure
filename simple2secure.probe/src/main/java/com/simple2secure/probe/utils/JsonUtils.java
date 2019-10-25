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
