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

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.OSInfo;
import com.simple2secure.api.model.Processor;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.rest.RESTUtils;
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
		String response = null;
		if (!Strings.isNullOrEmpty(ProbeConfiguration.probeId)) {
			response = RESTUtils.sendPost(LoadedConfigItems.getInstance().getBaseURL() + "/api/device/status/" + ProbeConfiguration.probeId, null,
					null);
		} else {
			response = RESTUtils.sendGet(LoadedConfigItems.getInstance().getBaseURL() + "/api/device/status");
		}
		if (!Strings.isNullOrEmpty(response)) {
			ProbeConfiguration.setAPIAvailablitity(true);
			log.info("SERVER REACHABLE!");
		} else {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("SERVER NOT REACHABLE!");
		}
	}
	
	
	public static void saveDeviceInfo(DeviceInfo deviceInfo) {
		String response = null;
		deviceInfo.setLastOnlineTimestamp(System.currentTimeMillis());
		response = RESTUtils.sendPost(LoadedConfigItems.getInstance().getBaseURL() + "/api/device/save", deviceInfo);
		if(!Strings.isNullOrEmpty(response)) {
			log.info("Device Information has been sent to portal!");
		}else {
			log.info("Problem occured while sending device information to portal!");
		}
	}

	public static String getOsinfo() {
		if (SystemUtils.IS_OS_WINDOWS) {
			return OSInfo.WINDOWS.name();
		} else if (SystemUtils.IS_OS_MAC) {
			return OSInfo.OSX.name();
		} else if (SystemUtils.IS_OS_LINUX) {
			return OSInfo.LINUX.name();
		} else {
			return OSInfo.UNKNOWN.name();
		}
	}
}
