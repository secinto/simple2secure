package com.simple2secure.probe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.util.Strings;

import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.api.model.Processor;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.gui.ProbeGUI;
import com.simple2secure.probe.license.LicenseController;

import ro.fortsoft.licensius.LicenseException;
import ro.fortsoft.licensius.LicenseNotFoundException;

public final class ProbeUtils {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

	// private ProbeUtilities() {};

	
	private static Logger log = LoggerFactory.getLogger(ProbeUtils.class);
	
	/**
	 * TODO: Write tests to check the different scenarios.
	 *
	 * This function unzips the imported license file and cheks if the extracted content is correct. If not the null will be returned, else
	 * the list of the files will be returned (public.key and certificate.dat)
	 *
	 * @param licenseZip
	 * @return
	 * @throws IOException
	 */
	public static List<File> unzipImportedFile(File importFile) throws IOException {
		if(importFile == null) {
			log.error("Importfile parameter should not be null.");
		}
		
		List<File> fileListUnzipped = new ArrayList<>();
		ZipInputStream zis = new ZipInputStream(new FileInputStream(importFile));

		ZipEntry ze = zis.getNextEntry();
		byte[] buffer = new byte[1024];
		int zipFileSize = 0;
		while (ze != null) {
			if (ze.isDirectory()) {
				break;
			} else {
				String fileName = ze.getName();
				File newFile = new File(fileName);

				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				fileListUnzipped.add(newFile);
				zipFileSize++;
				ze = zis.getNextEntry();
			}
		}
		zis.close();

		if (zipFileSize == 2) {
			log.debug("The files were unzipped successfully.");
			return fileListUnzipped;
		} else {
			log.error("A problem occured unzipping the files.");
			return null;
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
}