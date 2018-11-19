package com.simple2secure.probe.gui.controller;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.gui.ProbeGUI;
import com.simple2secure.probe.utilities.ImportFileManager;
import com.simple2secure.probe.utils.ProbeUtils;
import com.simple2secure.probe.utils.RequestHandler;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import ro.fortsoft.licensius.License;
import ro.fortsoft.licensius.LicenseException;
import ro.fortsoft.licensius.LicenseNotFoundException;

public class LicenseGUIController {
	
	private static Logger log = LoggerFactory.getLogger(LicenseGUIController.class);
	ImportFileManager iFM = new ImportFileManager();
	LicenseController licenseController = new LicenseController();

	@FXML
	private ImageView imageView;

	@FXML
	private Button importButton;

	@FXML
	private Label errorLabel;
	
	@FXML
	private void handleLicenseGUIImport() {
		
		//Transfers the zipped directory to the LicenseGUIController
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import your simple2secure license");
		ExtensionFilter extFilter = new ExtensionFilter("ZIP Files", "*.zip");
		fileChooser.getExtensionFilters().add(extFilter);
		File licenseZip = fileChooser.showOpenDialog(ProbeGUI.primaryStage);
		License downloadedLicense = null;
		CompanyLicenseObj licenseForAuth;
		String authToken;
		
		List<File> filesFromDir = null;
		
		//Unzips the zipped directory to a List
		try {
			filesFromDir = iFM.unzipImportedFile(licenseZip);
		} catch (IOException e) {
			errorLabel.setText("Problem occured while unzipping the zip file");
			log.error("Problem occured while unzipping the zip file");
		}
		
		//Load the license file from the imported directory
		if(licenseController.checkLicenseDirValidity(filesFromDir)) {
			try {
				downloadedLicense = licenseController.loadLocalLicense();
			} catch (LicenseNotFoundException | LicenseException e) {
				errorLabel.setText("A problem occured while loading the downloaded license.");
				log.error("A problem occured while loading the downloaded license.");
			}
		}else {
			errorLabel.setText("The directory you tried to import, does not contain the expected files.");
			log.error("The directory you tried to import, does not contain the expected files.");
		}
		
		if(!licenseController.checkLicenseProps(downloadedLicense)) {
			errorLabel.setText("Problem occured during the license validation. The server is not responding. Try again later!");
			log.error("Problem occured during the license validation. The server is not responding. Try again later!");
			importButton.setDisable(false);
		}
		
		licenseForAuth = licenseController.getLicenseForAuth(downloadedLicense);
		authToken = RequestHandler.sendPostReceiveResponse(ProbeConfiguration.getInstance().getLoadedConfigItems().getLicenseAPI() + "/activateProbe", licenseForAuth);
		if (Strings.isNullOrEmpty(authToken)) {
			errorLabel.setText("Problem occured during the license validation. The server is not responding. Try again later!");
			log.error("Problem occured during the license validation. The server is not responding. Try again later!");
			importButton.setDisable(false);
		}
			
		licenseController.activateLicenseInDB(authToken, licenseForAuth);
		
		ProbeConfiguration.authKey = authToken;
		ProbeConfiguration.probeId = licenseForAuth.getProbeId();
		ProbeConfiguration.setAPIAvailablitity(true);
		
		ProbeGUI.initRootPane();
	}

}
