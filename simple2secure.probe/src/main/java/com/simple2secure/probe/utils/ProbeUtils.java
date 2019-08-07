package com.simple2secure.probe.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.Step;

public class ProbeUtils {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

	/**
	 * This function returns the file file from the file list according to the
	 * filename
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

	public static Processor getProcessorFromListByName(String name, Map<String, Processor> list) {
		for (Processor item : list.values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	public static Step getStepFromListByName(String name, List<Step> list) {
		for (Step item : list) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}
}
