package com.simple2secure.portal.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.file.ZIPUtils;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.commons.license.LicenseDateUtil;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.LicenseActivation;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LicenseUtils extends BaseServiceProvider {
	@Value("${license.filepath}")
	private String licenseFilePath;
	
	private static String SERVICE_RELEASE_DIR = "../simple2secure.service/release";

   /**
    * Activates the provided license and creates a private license {@link CompanyLicensePrivate} and stores it in the database. If the
    * license has not been activated before, it creates a secret for this license otherwise just a new access token is created. Thus, it is
    * possible to perform this operation multiple times in order to provide a long term access token renewal process. Depending if the call
    * is coming from a PROBE or a POD different tables and authentications are created.
    *
    * @param licensePublic
    *           The public license object which must be provided in order to activate it and generate an access token.
    * @param podActivation
    *           True if the activation is coming from a POD, otherwise false.
    * @return
    * @throws UnsupportedEncodingException
    */
   public LicenseActivation authenticateLicense(CompanyLicensePublic licensePublic, String locale) throws UnsupportedEncodingException {
      LicenseActivation activation = new LicenseActivation(false);

      // TODO: delete or put in other place, not necessary here in this place
      // activation.setMessage(messageByLocaleService.getMessage("problem_during_activation", locale));

      if (licensePublic != null) {
         String groupId = licensePublic.getGroupId();
         String licenseId = licensePublic.getLicenseId();
         String deviceId = licensePublic.getDeviceId();

         if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(licenseId) && !Strings.isNullOrEmpty(deviceId)) {
            CompanyGroup group = groupRepository.find(groupId);
            CompanyLicensePrivate license = null;
            /*
             * Check if a license has already been activated and associated with this POD or PROBE
             */
            license = licenseRepository.findByLicenseIdAndDeviceId(licenseId, deviceId);

            /*
             * If no license has been activated and associated with the POD or PROBE we need to create one for them.
             */
            if (license == null) {
               List<CompanyLicensePrivate> licenses = licenseRepository.findByLicenseId(licenseId);
               if (licenses != null && licenses.size() > 0) {
                  CompanyLicensePrivate tempLicense = licenses.get(licenses.size() - 1);
                  if (!Strings.isNullOrEmpty(tempLicense.getDeviceId())) {
                     license = tempLicense.copyLicense();
                  } else {
                     license = tempLicense;
                  }
               }
            }

            /*
             * Create a new access token for the POD or PROBE and update the license
             */
            if (group != null && license != null) {
               if (license.getLastTokenRefresh() == 0
                     || license.getLastTokenRefresh() + StaticConfigItems.tokenValidity < System.currentTimeMillis()) {
                  license.setTokenSecret(RandomStringUtils.randomAlphanumeric(20));

                  String accessToken = null;

                  accessToken = tokenAuthenticationService.addDeviceAuthentication(deviceId, group, license);

                  if (!Strings.isNullOrEmpty(accessToken)) {
                     if (Strings.isNullOrEmpty(license.getDeviceId())) {
                        license.setDeviceId(deviceId);
                     }
                     license.setAccessToken(accessToken);
                     license.setLastTokenRefresh(System.currentTimeMillis());
                     license.setActivated(true);
                     licenseRepository.save(license);
                  }
               }
               activation.setAccessToken(license.getAccessToken());
               activation.setSuccess(true);
               return activation;

            } else {
               activation.setMessage("specified_license_not_available");
            }
         } else {
            activation.setMessage("specified_license_not_available");
         }
      }
      return activation;
   }

   public String getPayloadFromTheToken(String token) {
      String[] split_string = token.split("\\.");
      String base64EncodedBody = split_string[1];

      String payload = new String(Base64.getDecoder().decode(base64EncodedBody));

      return payload;

   }

   public String getFieldFromPayload(String payload, String field) {
      JsonNode node = JSONUtils.fromString(payload);
      return node.findValue(field).asText();
   }
   
	/**
	 * This function creates temporary directory and puts a valid license file and the setup_s2s_probe.exe in it. Further it zips the directory and returns
	 * the Bytearray of the zipped directory and last, deletes the temporary created directory.
	 *
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public byte[] downloadFile(String contextId, String groupId) throws IOException, URISyntaxException {
		String tmpDirPath = System.getProperty("java.io.tmpdir");
		File tmpDir = new File(tmpDirPath);
		File newFolder = new File(tmpDir, "probe");
		newFolder.mkdir();
		File releaseDir = new File(newFolder, "release");
		releaseDir.mkdir();
		File licenseZip = new File(releaseDir, "license-" + groupId + ".zip");
		ByteArrayOutputStream licenseInByte = generateLicenseForPackage(contextId, groupId);
		OutputStream os = new FileOutputStream(licenseZip);
		licenseInByte.writeTo(os);
		os.close();
		licenseInByte.close();
		for(File file : getFilesForProbePackage()) {
			if(file.getName().equals("setup_s2s_probe.exe")) {
				File tmp = new File(releaseDir.getAbsolutePath() + File.separator + file.getName());
				FileUtils.copyFile(file, tmp);
			}
		}
		
		ByteArrayOutputStream zippedProbeDir = ZIPUtils.createZIPStreamFromFiles(Arrays.asList(newFolder.listFiles()));
		
		deleteTmpProbeDir(newFolder);

		return zippedProbeDir.toByteArray();
	}
	
	
	
	/**
	 * This function deletes the provided file or directory. If the target is a directory this function will delete all subdirectories and files recursively.
	 * 
	 * @param file The file to delete.
	 */
	public void deleteTmpProbeDir(File file) {
		if(file != null) {
			if(file.isDirectory()) {
				for(File fileToDelete : file.listFiles()) {
					if(fileToDelete.isDirectory()) {
						deleteTmpProbeDir(fileToDelete);
					}else {
						fileToDelete.delete();
					}
				}
			}
			file.delete();
		}
	}
	
	
	
	/**
	 * This function gets the files from the release dir in the simple2secure.service project.
	 * 
	 * @return List of files from the release directory in the simple2secure.service project.
	 */
	public File[] getFilesForProbePackage() {
		File serviceReleaseDir = new File(SERVICE_RELEASE_DIR);
		File[] serviceDirFiles = serviceReleaseDir.listFiles();
		return serviceDirFiles;
	}
   
   public ByteArrayOutputStream generateLicenseForPackage(String contextId, String groupId) {
	   Context context = contextRepository.find(contextId);
	   ByteArrayOutputStream byteArrayOutputStream = null;
	   if(context != null) {
		   LicensePlan licensePlan = licensePlanRepository.find(context.getLicensePlanId());
		   
		   if (context.getCurrentNumberOfLicenseDownloads() < licensePlan.getMaxNumberOfDownloads()) {

				String expirationDate = LicenseDateUtil.getLicenseExpirationDate(licensePlan.getValidity(), licensePlan.getValidityUnit());
				
				 //TODO: Generates a new license for each request. Should not be the case
				 
				List<CompanyLicensePrivate> companyLicenses = licenseRepository.findAllByGroupId(groupId);
				String licenseId = LicenseUtil.generateLicenseId();
				CompanyLicensePrivate companyLicense = new CompanyLicensePrivate(groupId, licenseId, expirationDate);

				if (companyLicenses != null && companyLicenses.size() > 0) {
					licenseId = companyLicenses.get(companyLicenses.size() - 1).getLicenseId();
					companyLicense = new CompanyLicensePrivate(groupId, licenseId, expirationDate);
				} else {
					licenseRepository.save(companyLicense);
				}
				
				try {
					String licenseFile = LicenseUtil.createLicenseFile(companyLicense.getGroupId(), companyLicense.getLicenseId(),
							companyLicense.getExpirationDate());

					byteArrayOutputStream = LicenseUtil.generateLicenseZIPStreamFromFile(licenseFile,
							licenseFilePath + "public.key");

				}catch(IOException e) {
					
				}
				context.setCurrentNumberOfLicenseDownloads(context.getCurrentNumberOfLicenseDownloads() + 1);
				try {
					contextRepository.update(context);
				}catch(ItemNotFoundRepositoryException e) {
					
				}
			}
	   }
	   
	   return byteArrayOutputStream;
   }
}
