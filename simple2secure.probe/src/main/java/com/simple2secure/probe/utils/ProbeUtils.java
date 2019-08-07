package com.simple2secure.probe.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.Processor;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.network.NetUtils;
import com.simple2secure.probe.config.ProbeConfiguration;

public final class ProbeUtils {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

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

	public static Date convertStringtoDate(String date) {
		try {
			return DATE_FORMAT.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This function checks if the server is reachable
	 */
	public static void isServerReachable() {
		if (NetUtils.netIsAvailable(LoadedConfigItems.getInstance().getBaseURL())) {
			ProbeConfiguration.setAPIAvailablitity(true);
			log.info("SERVER REACHABLE!");
		} else {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("SERVER NOT REACHABLE!");
		}
	}

}