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
package com.simple2secure.probe.gui.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.gui.ProbeGUI;
import com.simple2secure.probe.gui.controller.ProbeController;
import com.simple2secure.probe.utils.LocaleHolder;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class ViewNavigator {

	/**
	 * Convenience constants for fxml layouts managed by the navigator.
	 */
	public static final String MAIN = "/fxml/ProbeRoot.fxml";
	public static final String LICENSE_VIEW = "/fxml/LicenseView.fxml";

	private static Map<String, FXMLLoader> loaderCache = new HashMap<>();

	private static Logger log = LoggerFactory.getLogger(ViewNavigator.class);

	/** The main application layout controller. */
	private static ProbeController mainController;

	/**
	 * Stores the main controller for later use in navigation tasks.
	 *
	 * @param mainController
	 *          the main application layout controller.
	 */
	public static void setMainController(ProbeController mainController) {
		ViewNavigator.mainController = mainController;
	}

	/***
	 * Redirects to a page without doing any PathTracking handling.
	 *
	 * @param fxml
	 */
	public static void loadView(String fxml) {
		try {
			FXMLLoader loader;
			Node n;
			if ((loader = loaderCache.get(fxml)) == null) {
				loader = new FXMLLoader(ProbeGUI.class.getResource(fxml));
				n = loader.load();
				loaderCache.put(fxml, loader);
			} else {
				n = loader.getRoot();
			}

			if (!n.equals(mainController.getView())) {
				loader.setResources(ResourceBundle.getBundle("messageCodes", LocaleHolder.getLocale()));

				mainController.setView(n);
			}
		} catch (IOException e) {
			log.debug(LocaleHolder.getMessage("view_load_error").getMessage());
		}
	}
}
