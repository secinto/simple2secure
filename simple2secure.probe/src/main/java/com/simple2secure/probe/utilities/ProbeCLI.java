package com.simple2secure.probe.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.CompanyLicenseObj;

public class ProbeCLI {
	private static Logger log = LoggerFactory.getLogger(ImportFileManager.class);


	public static void main(String[] args) {
		String importFilePath;
		
		if(args.length != 2) {
			log.error("Invalid amount of parameters.");
		}
		else {
			importFilePath = args[1].toString();
				
			CompanyLicenseObj licenseFile = ProbeUtilities.loadLicenseFromPath("K:\\work\\projects\\simple2secure\\license-5be43bd01dbe9510b818e4de.zip");
			
			if (licenseFile != null)
				log.debug("The license file was imported succesfully.");
		}
	}
}
