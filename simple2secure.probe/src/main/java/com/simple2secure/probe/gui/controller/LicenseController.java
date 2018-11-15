package com.simple2secure.probe.gui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.gui.ProbeGUI;
import com.simple2secure.probe.utils.RequestHandler;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.ProbeUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import ro.fortsoft.licensius.License;
import ro.fortsoft.licensius.LicenseException;
import ro.fortsoft.licensius.LicenseManager;
import ro.fortsoft.licensius.LicenseNotFoundException;

public class LicenseController implements Initializable {

	private static Logger log = LoggerFactory.getLogger(LicenseController.class);

	@FXML
	private ImageView imageView;

	@FXML
	private Button importButton;

	@FXML
	private Label errorLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	/**
	 * TODO: Write tests to check the different scenarios. This is the function which sets error/success messages according to the response
	 * from the server!
	 *
	 * @param event
	 * @throws IOException
	 * @throws LicenseException
	 * @throws LicenseNotFoundException
	 * @throws InterruptedException
	 */
	//TODO: Move all GUI-elements from this class to the LicenseGUIController
	@FXML
	private void handleLicenseImport() throws IOException, LicenseNotFoundException, LicenseException, InterruptedException {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import your simple2secure license");
		ExtensionFilter extFilter = new ExtensionFilter("ZIP Files", "*.zip");
		fileChooser.getExtensionFilters().add(extFilter);
		File licenseZip = fileChooser.showOpenDialog(ProbeGUI.primaryStage);
		if (licenseZip != null) {
			String fileName = licenseZip.getName();
			importButton.setDisable(true);
			errorLabel.setText("File imported successfully: " + fileName + " - Validating the license...");
			//TODO: unzipLicense will be outsourced to ImportFileManager
			List<File> unzippedFiles = unzipLicense(licenseZip);

			if (unzippedFiles == null) {
				importButton.setDisable(false);
			} else {
				File licenseFile = ProbeUtils.getFileFromListByName(unzippedFiles, "license.dat");
				if (licenseFile == null) {
					errorLabel.setText("Error occured during validation process. Please try again!");
					importButton.setDisable(false);
				} else {
					License localLicense = loadLocalLicense();
					boolean isValid = checkLicenseValidity(localLicense);
					if (!isValid) {
						errorLabel.setText("Provided license is not valid. Please try it again with the new one!");
						importButton.setDisable(false);
					} else {
						CompanyLicenseObj license = ProbeGUI.getLicenseFromDb();
						/*
						 * Here we create the unique ID for the probe.
						 *
						 * TODO: This must be checked if it is the correct place. Should also work without license view.
						 */
						String probeId = UUID.randomUUID().toString();

						if (license != null) {
							if (!Strings.isNullOrEmpty(license.getProbeId())) {
								probeId = license.getProbeId();
							}
						}
						/*
						 * Obtain parameters from the license itself.
						 */
						String groupId = map.get("groupId");
						String licenseId = map.get("licenseId");
						String expirationDate = map.get("expirationDate");
						/*
						 * This verification should never be invalid
						 */
						if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(licenseId) && !Strings.isNullOrEmpty(expirationDate)) {
							if (license == null) {
								license = new CompanyLicenseObj(groupId, probeId, licenseId, expirationDate);
							} else {
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

								ProbeGUI.initRootPane();

							} else {
								errorLabel.setText("Problem occured during the license validation. The server is not responding. Try again later!");
								importButton.setDisable(false);
							}

						} else {
							errorLabel.setText("Problem occured during the license validation. Please try it again with the new one!");
							importButton.setDisable(false);
						}

					}
				}

			}
		} else {
			errorLabel.setText("Problem occured while importing license file");
			log.error("Problem occured while importing license file");
		}
	}


	/**
	 * Checks the validity of the license. 
	 *
	 * @param	...License object
	 * @return 	...Boolean isLicenseValid
	 * @throws 	...LicenseException
	 * @throws 	...LicenseNotFoundException
	 */
	public boolean checkLicenseValidity(License license) throws LicenseNotFoundException, LicenseException {
		return LicenseManager.getInstance().isValidLicense(license);
	}
	
	/**
	 * Loads the license from the root directory of the project. 
	 *
	 * @param 
	 * @return 	...true if the license is valid, false if it is not valid
	 * @throws 	...LicenseException
	 * @throws 	...LicenseNotFoundException
	 */
	public License loadLocalLicense() throws LicenseNotFoundException, LicenseException {
		return LicenseManager.getInstance().getLicense();
	}

	/**
	 * Creates a new entry for the license in the DB. 
	 *
	 * @param 	...groupId
	 * @param 	...licenseId
	 * @param 	...expirationDate
	 */
	public void createNewCompanyLicenseObjectInDB(String groupId, String licenseId, String expirationDate) {
		String probeId = UUID.randomUUID().toString();
		CompanyLicenseObj cLO = new CompanyLicenseObj(groupId, probeId, licenseId, expirationDate);
		DBUtil.getInstance().merge(cLO);
	}
	
	/**
	 * Loads the license from the data base. 
	 * 
	 * @return 	...a CompanyLicenseObject
	 */
	public static CompanyLicenseObj loadLicenseFromDb() {
		List<CompanyLicenseObj> licenses = DBUtil.getInstance().findAll(new CompanyLicenseObj());

		if (licenses.size() != 1) {
			return null;
		} else {
			return licenses.get(0);
		}

	}

	/**
	 * Checks if a license already exists in the DB. 
	 * 
	 * @return 	...true if already a license exists in DB, false if there is no license in the DB
	 */
	public boolean licenseExistsInDB() {
		boolean licenseExistsInDB = false;
		
		if(loadLicenseFromDb() != null)
			licenseExistsInDB = true;
		
		return licenseExistsInDB;
	}
	
	/**
	 * Checks if the license directory contains the right files. 
	 *
	 * @param   ...List<File> licenseDir
	 * @return 	...true if the directory contains the right files, false if it does not contain the right files
	 */
	public boolean checkLicenseDirValidity(List<File> licenseDir) {
		boolean isValidLicenseDir = false;
		boolean licenseFile = licenseDir.contains("license.dat");
		boolean publicKeyFile = licenseDir.contains("public.key");
		
		if(licenseFile && publicKeyFile)
			isValidLicenseDir = true;
		
		return isValidLicenseDir;
	}

	/**
	 * Checks if the properties in the license object are set (not null or empty). 
	 *
	 * @param   ...License object
	 * @return 	...true if the properties in the license object are set, false if only one property is null or empty
	 */
	public boolean checkLicenseProps(License license) {
		Boolean isLicensePropsValid = false;
		if(!Strings.isNullOrEmpty(license.getFeature("probeId")) && !Strings.isNullOrEmpty(license.getFeature("groupId")) && !Strings.isNullOrEmpty(license.getFeature("licenseId")) && !Strings.isNullOrEmpty(license.getExpirationDateAsString())) {
			isLicensePropsValid = true;
		}
		return isLicensePropsValid;
	}
	
	/**
	 * Updates the license in the DB with the local license. 
	 *
	 * @param   ...License object
	 */
	public void updateDBFromLocalLicense(License license) {
		CompanyLicenseObj dbLicense = loadLicenseFromDb();
		dbLicense.setGroupId(license.getFeature("groupId"));
		//dbLicense.setProbeId(license.getFeature("probeId"));
		dbLicense.setLicenseId(license.getFeature("licenseId"));
		dbLicense.setExpirationDate(license.getFeature("expirationDate"));
		
		DBUtil.getInstance().merge(dbLicense);
	}
	
	//TODO: This method seems to be unnecessary so it should be removed
	/**
	 * Maps the license object to a hashmap. 
	 *
	 * @param 	...License object
	 * @return 	...hashMap with the mapped license properties in it.
	 * @throws 	...InvalidObjectException
	 */
	public Map<String, String> mapLicenseToMap(License license) throws InvalidObjectException {
		
		Map<String, String> mappedLicense = new HashMap<String, String>();
		String groupId = license.getFeature("groupId");
		String licenseId = license.getFeature("licenseId");
		String expirationDate = license.getExpirationDateAsString();

		if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(licenseId) && !Strings.isNullOrEmpty(expirationDate)) {
			mappedLicense.put("groupId", groupId);
			mappedLicense.put("licenseId", licenseId);
			mappedLicense.put("expirationDate", expirationDate);
		} else {
			throw new InvalidObjectException("The license object does not provide enough Information to map.");
		}	
		return mappedLicense;
	}
}
