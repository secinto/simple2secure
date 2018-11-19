package com.simple2secure.probe.gui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
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
	private void handleLicenseImport(String filePath) throws IOException, LicenseNotFoundException, LicenseException, InterruptedException {
	
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
	public void updateLicenseInDB(CompanyLicenseObj license) {
		DBUtil.getInstance().merge(license);
	}

	//TODO: Possibly null check required
	/**
	 * Loads the license from the data base. 
	 * 
	 * @return 	...a CompanyLicenseObject
	 */
	public CompanyLicenseObj loadLicenseFromDB() {
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
		
		if(loadLicenseFromDB() != null)
			licenseExistsInDB = true;
		
		return licenseExistsInDB;
	}

	/**
	 * Sets the needed Flags in the DB to mark the license activated.
	 *
	 * @param license  	...CompanyLicenseObj
	 * @param authToken	...the token which the server returns if the activation succeeded.
	 *
	 */
	public void activateLicenseInDB(String authToken, CompanyLicenseObj license) {
		license.setAuthToken(authToken);
		license.setActivated(true);
		updateLicenseInDB(license);
	}
	
	//TODO: Possibly null check required
	/**
	 * Creates a CompanyLicenseObj to send it to the server for authentication.
	 * Also checks if there is already a license stored in the DB, if there is
	 * a license stored it just updates the license. If there is no license
	 * stored in the DB, it creates a new entry in the DB. 
	 *
	 * @param   ...License object
	 * @return 	...CompanyLicenseObj for authentication.
	 *
	 */
	public CompanyLicenseObj getLicenseForAuth(License license) {
		String probeId = ""; 
		String groupId, licenseId, expirationDate;
		CompanyLicenseObj result;
		
		groupId = license.getFeature("groupId");
		licenseId = license.getFeature("licenseId");
		expirationDate = license.getExpirationDateAsString();
		
		if(licenseExistsInDB()) {
			result = loadLicenseFromDB();
			if(Strings.isNullOrEmpty(result.getProbeId())) {
				probeId =  UUID.randomUUID().toString();
				result.setProbeId(probeId);
			}
			result.setGroupId(groupId);
			result.setLicenseId(licenseId);
			result.setExpirationDate(expirationDate);
		}else {
			probeId =  UUID.randomUUID().toString();
			result = new CompanyLicenseObj(groupId, probeId, licenseId, expirationDate);
		}
		
		updateLicenseInDB(result);
		return result;
	}
	
	/**
	 * Checks if a license is expired. 
	 * 
	 * @return 	...true if the license is expired, false if the license is not expired
	 */
	public boolean isLicenseExpired(CompanyLicenseObj license) {
		return System.currentTimeMillis() > ProbeUtils.convertStringtoDate(license.getExpirationDate()).getTime();
	}
}
