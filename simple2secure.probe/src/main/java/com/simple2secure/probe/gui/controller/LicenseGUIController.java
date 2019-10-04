/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
package com.simple2secure.probe.gui.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.file.ZIPUtils;
import com.simple2secure.commons.license.License;
import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.commons.rest.RESTUtils;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.gui.ProbeGUI;
import com.simple2secure.probe.license.LicenseController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class LicenseGUIController {

	private static Logger log = LoggerFactory.getLogger(LicenseGUIController.class);
	LicenseController licenseController = new LicenseController();

	@FXML
	private ImageView imageView;

	@FXML
	private Button importButton;

	@FXML
	private Label errorLabel;

	@FXML
	private void handleLicenseGUIImport() {

		// Transfers the zipped directory to the LicenseGUIController
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import your simple2secure license");
		ExtensionFilter extFilter = new ExtensionFilter("ZIP Files", "*.zip");
		fileChooser.getExtensionFilters().add(extFilter);
		File licenseZip = fileChooser.showOpenDialog(ProbeGUI.primaryStage);
		License downloadedLicense = null;
		CompanyLicensePublic licenseForAuth;
		String authToken;

		List<File> filesFromDir = null;

		// Unzips the zipped directory to a List
		try {
			filesFromDir = ZIPUtils.unzipImportedFile(licenseZip);
		} catch (IOException e) {
			errorLabel.setText("Problem occured while unzipping the zip file");
			log.error("Problem occured while unzipping the zip file");
		}

		// Load the license file from the imported directory
		if (LicenseUtil.checkLicenseDirValidity(filesFromDir)) {
			try {
				for (File file : filesFromDir) {
					if (file.getName().equals(LicenseUtil.licenseFileName)) {
						downloadedLicense = LicenseUtil.getLicense(file);
					}
				}
			} catch (Exception e) {
				errorLabel.setText("A problem occured while loading the downloaded license.");
				log.error("A problem occured while loading the downloaded license.");
			}
		} else {
			errorLabel.setText("The directory you tried to import, does not contain the expected files.");
			log.error("The directory you tried to import, does not contain the expected files.");
		}

		if (!licenseController.checkLicenseProps(downloadedLicense)) {
			errorLabel.setText("Problem occured during the license validation. The server is not responding. Try again later!");
			log.error("Problem occured during the license validation. The server is not responding. Try again later!");
			importButton.setDisable(false);
		}

		licenseForAuth = licenseController.createLicenseForAuth(downloadedLicense);
		authToken = RESTUtils.sendPost(LoadedConfigItems.getInstance().getLicenseAPI() + "/activateProbe", licenseForAuth,
				ProbeConfiguration.authKey);
		if (Strings.isNullOrEmpty(authToken)) {
			errorLabel.setText("Problem occured during the license validation. The server is not responding. Try again later!");
			log.error("Problem occured during the license validation. The server is not responding. Try again later!");
			importButton.setDisable(false);
		}

		licenseController.activateLicenseInDB(authToken, licenseForAuth);

		ProbeConfiguration.authKey = authToken;
		ProbeConfiguration.probeId = licenseForAuth.getDeviceId();
		ProbeConfiguration.groupId = licenseForAuth.getGroupId();
		ProbeConfiguration.setAPIAvailablitity(true);

		ProbeGUI.initRootPane();
	}

}
