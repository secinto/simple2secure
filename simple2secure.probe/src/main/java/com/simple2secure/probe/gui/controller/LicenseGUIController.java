package com.simple2secure.probe.gui.controller;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.gui.ProbeGUI;
import com.simple2secure.probe.utilities.ImportFileManager;
import com.simple2secure.probe.utils.ProbeUtils;

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
		License license = null;
		Map<String, String> mappedLicense;
		
		Boolean isValidLicenseDir;
		List<File> filesFromDir = null;
		
		
		//Unzips the zipped directory to a List
		try {
			filesFromDir = iFM.unzipImportedFile(licenseZip);
		} catch (IOException e) {
			errorLabel.setText("Problem occured while unzipping the zip file");
			log.error("Problem occured while unzipping the zip file");
		}
		
		//Checks if the unzipped license directory contains the expected files.
		isValidLicenseDir = licenseController.checkLicenseDirValidity(filesFromDir);
		
		//Load the license file from the imported directory
		if(isValidLicenseDir) {
			try {
				license = licenseController.loadLocalLicense();
			} catch (LicenseNotFoundException | LicenseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			errorLabel.setText("The directory you tried to import, does not contain the expected files.");
			log.error("The directory you tried to import, does not contain the expected files.");
		}
		
		try {
			mappedLicense = licenseController.mapLicenseToMap(license);
		} catch (InvalidObjectException e) {
			errorLabel.setText("The directory you tried to import, does not contain the expected files.");
			log.error("The directory you tried to import, does not contain the expected files.");
		}
		
		
		
		
	}

}
