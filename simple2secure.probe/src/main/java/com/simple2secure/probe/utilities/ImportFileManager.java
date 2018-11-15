package com.simple2secure.probe.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import ro.fortsoft.licensius.License;
import ro.fortsoft.licensius.LicenseException;
import ro.fortsoft.licensius.LicenseManager;
import ro.fortsoft.licensius.LicenseNotFoundException;

public class ImportFileManager {
	
	private static Logger log = LoggerFactory.getLogger(ImportFileManager.class);
	
	public ImportFileManager() {}
	
	
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
	public List<File> unzipImportedFile(File importFile) throws IOException {
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
	
	/**
	 * This function checks if the imported license is still valid. If it is valid some data should be sent to the portal in order to map this
	 * probe with the groupId which is provided in the license.dat file.
	 *
	 * @param license
	 * @return
	 * @throws LicenseException
	 * @throws LicenseNotFoundException
	 */
	Map<String, String> checkLicense(File license) throws LicenseNotFoundException, LicenseException {
		License licenseLocal = LicenseManager.getInstance().getLicense();
		Map<String, String> map = new HashMap<String, String>();
		boolean validLicense = LicenseManager.getInstance().isValidLicense(licenseLocal);
		if (validLicense) {
			log.debug("License is valid.");
			String groupId = licenseLocal.getFeature("groupId");
			String licenseId = licenseLocal.getFeature("licenseId");
			String expirationDate = licenseLocal.getExpirationDateAsString();

			if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(licenseId) && !Strings.isNullOrEmpty(expirationDate)) {
				map.put("groupId", groupId);
				map.put("licenseId", licenseId);
				map.put("expirationDate", expirationDate);
				return map;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
