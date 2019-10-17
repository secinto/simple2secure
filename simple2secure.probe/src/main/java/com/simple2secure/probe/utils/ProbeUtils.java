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

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.Processor;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.network.NetUtils;
import com.simple2secure.probe.config.ProbeConfiguration;

public final class ProbeUtils {

	private static Logger log = LoggerFactory.getLogger(ProbeUtils.class);

	public static Processor getProcessorFromListByName(String name, Map<String, Processor> list) {
		for (Processor item : list.values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * This function returns the file file from the file list according to the filename
	 *
	 * @param files
	 * @param fileName
	 * @return
	 */
	public static File getFileFromListByName(List<File> files, String fileName) {
		for (File file : files) {
			if (file.getName().equals(fileName)) {
				return file;
			}
		}
		return null;
	}

	/**
	 * This function checks if the server is reachable
	 */
	public static void isServerReachable() {
		if (NetUtils.netIsAvailable(LoadedConfigItems.getInstance().getBaseURL() + "/api/service")) {
			ProbeConfiguration.setAPIAvailablitity(true);
			log.info("SERVER REACHABLE!");
		} else {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("SERVER NOT REACHABLE!");
		}
	}

}