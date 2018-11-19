package com.simple2secure.probe.utilities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.util.Strings;

import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.gui.ProbeGUI;
import com.simple2secure.probe.gui.controller.LicenseController;
import com.simple2secure.probe.utils.RequestHandler;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.ProbeUtils;

import ro.fortsoft.licensius.LicenseException;
import ro.fortsoft.licensius.LicenseNotFoundException;

public final class ProbeUtilities {
	
	private ProbeUtilities() {};
	
	private static CompanyLicenseObj license;
	private static ImportFileManager iFM =  new ImportFileManager();
	private static Logger log = LoggerFactory.getLogger(ProbeUtilities.class);
	private LicenseController licenseCon;

	
	public  CompanyLicenseObj loadLicenseFromPath(String importFilePath) {
		
		//License parameters
		String probeId = UUID.randomUUID().toString();
		String groupId;
		String licenseId;
		String expirationDate;
		
		File inputFile = new File(importFilePath);
		Map<String, String> map;
		
		if (inputFile != null) {
			List<File> unzippedFiles;
			try {
				unzippedFiles = iFM.unzipImportedFile(inputFile);

				File licenseFile = ProbeUtils.getFileFromListByName(unzippedFiles, "license.dat");
				map = iFM.checkLicense(licenseFile);
				
				license = licenseCon.loadLicenseFromDB(); 
				
				if(license != null)
					if(Strings.isNotNullAndNotEmpty(license.getProbeId()))
						probeId = license.getProbeId();
				
				groupId = map.get("groupId");
				licenseId = map.get("licenseId");
				expirationDate = map.get("expirationDate");
				
				if (Strings.isNotNullAndNotEmpty(groupId) && Strings.isNotNullAndNotEmpty(licenseId) && Strings.isNotNullAndNotEmpty(expirationDate)) {
					if (license == null)
						license = new CompanyLicenseObj(groupId, probeId, licenseId, expirationDate);
					
					license.setGroupId(groupId);
					license.setProbeId(probeId);
					license.setLicenseId(licenseId);
					license.setExpirationDate(expirationDate);
				}
				
				String authToken = RequestHandler.sendPostReceiveResponse(
						ProbeConfiguration.getInstance().getLoadedConfigItems().getLicenseAPI() + "/activateProbe", license);

				if (!Strings.isNullOrEmpty(authToken)) {
					license.setAuthToken(authToken);
					license.setActivated(true);
					DBUtil.getInstance().merge(license);

					ProbeConfiguration.authKey = authToken;
					ProbeConfiguration.probeId = probeId;

					license = ProbeGUI.getLicenseFromDb();

					ProbeConfiguration.setAPIAvailablitity(true);
				} else {
					log.error("Problem occured during the license validation. The server is not responding. Try again later!");
				}
			}catch(LicenseNotFoundException e) {
				throw new IllegalArgumentException("File not found!", e);
			}catch(LicenseException e) {
				throw new IllegalArgumentException("The file you provided is not a valid license file.");
			}catch(IOException e) {
				throw new IllegalArgumentException("Failed to unzip license folder");
			}
		}
		
		return license;
	}
}