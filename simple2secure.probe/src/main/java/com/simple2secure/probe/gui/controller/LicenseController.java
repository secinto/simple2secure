package com.simple2secure.probe.gui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.simple2secure.api.config.ConfigItems;
import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.gui.ProbeGUI;
import com.simple2secure.probe.utils.APIUtils;
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
	 * TODO: Write tests to check the different scenarios. This is the function
	 * which sets error/success messages according to the response from the server!
	 *
	 * @param event
	 * @throws IOException
	 * @throws LicenseException
	 * @throws LicenseNotFoundException
	 * @throws InterruptedException
	 */

	@FXML
	private void handleLicenseImport(ActionEvent event)
			throws IOException, LicenseNotFoundException, LicenseException, InterruptedException {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import your simple2secure license");
		ExtensionFilter extFilter = new ExtensionFilter("ZIP Files", "*.zip");
		fileChooser.getExtensionFilters().add(extFilter);
		File licenseZip = fileChooser.showOpenDialog(ProbeGUI.primaryStage);
		if (licenseZip != null) {
			String fileName = licenseZip.getName();
			importButton.setDisable(true);
			errorLabel.setText("File imported successfully: " + fileName + " - Validating the license...");
			List<File> unzippedFiles = unzipLicense(licenseZip);

			if (unzippedFiles == null) {
				importButton.setDisable(false);
			} else {
				File licenseFile = ProbeUtils.getFileFromListByName(unzippedFiles, "license.dat");
				if (licenseFile == null) {
					errorLabel.setText("Error occured during validation process. Please try again!");
					importButton.setDisable(false);
				} else {
					Map<String, String> map = checkLicense(licenseFile);
					if (map == null) {
						errorLabel.setText("Provided license is not valid. Please try it again with the new one!");
						importButton.setDisable(false);
					} else {
						// LicenseDaoImpl licenseDao = new LicenseDaoImpl();
						CompanyLicenseObj license = ProbeGUI.getLicenseFromDb();
						String probeId = UUID.randomUUID().toString();

						if (license != null) {
							if (!Strings.isNullOrEmpty(license.getProbeId())) {
								probeId = license.getProbeId();
							}
						}

						String groupId = map.get("groupId");
						String licenseId = map.get("licenseId");
						String expirationDate = map.get("expirationDate");

						if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(licenseId)
								&& !Strings.isNullOrEmpty(expirationDate)) {
							if (license == null) {
								license = new CompanyLicenseObj(groupId, probeId, licenseId, expirationDate);
							} else {
								license.setGroupId(groupId);
								license.setProbeId(probeId);
								license.setLicenseId(licenseId);
								license.setExpirationDate(expirationDate);
							}

							String authToken = APIUtils.sendPostWithResponse(ConfigItems.license_api + "/activateProbe", license);

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
								errorLabel.setText(
										"Problem occured during the license validation. The server is not responding. Try again later!");
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
	 * TODO: Write tests to check the different scenarios.
	 * 
	 * This function unzips the imported license file and cheks if the extracted
	 * content is correct. If not the null will be returned, else the list of the
	 * files will be returned (public.key and certificate.dat)
	 * 
	 * @param licenseZip
	 * @return
	 * @throws IOException
	 */
	private List<File> unzipLicense(File licenseZip) throws IOException {
		List<File> licenseFiles = new ArrayList<>();
		ZipInputStream zis = new ZipInputStream(new FileInputStream(licenseZip));

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
				licenseFiles.add(newFile);
				zipFileSize++;
				ze = zis.getNextEntry();
			}

		}

		zis.close();

		if (zipFileSize == 2) {
			errorLabel.setText("Files have been extracted successfully from the zip archive!");
			return licenseFiles;
		} else {
			errorLabel.setText("Archive is damaged, not able to read the files from it!");
			return null;
		}
	}

	/**
	 * This function checks if the imported license is still valid. If it is valid
	 * some data should be sent to the portal in order to map this probe with the
	 * groupId which is provided in the license.dat file.
	 * 
	 * @param license
	 * @return
	 * @throws LicenseException
	 * @throws LicenseNotFoundException
	 */
	public Map<String, String> checkLicense(File license) throws LicenseNotFoundException, LicenseException {
		License license1 = LicenseManager.getInstance().getLicense();
		Map<String, String> map = new HashMap<String, String>();
		boolean validLicense = LicenseManager.getInstance().isValidLicense(license1);
		if (validLicense) {
			String groupId = license1.getFeature("groupId");
			String licenseId = license1.getFeature("licenseId");
			String expirationDate = license1.getExpirationDateAsString();

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
