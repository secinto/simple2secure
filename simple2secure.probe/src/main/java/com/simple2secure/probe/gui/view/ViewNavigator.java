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
	public static final String LOGIN_VIEW = "/fxml/LoginView.fxml";
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
