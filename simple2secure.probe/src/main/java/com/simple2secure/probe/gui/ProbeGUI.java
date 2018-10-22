package com.simple2secure.probe.gui;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.config.ConfigItems;
import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.commons.general.TimingUtils;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.gui.view.ViewNavigator;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.LocaleHolder;
import com.simple2secure.probe.utils.ProbeUtils;

import ch.qos.logback.classic.Level;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class ProbeGUI extends Application {

	private static Logger log = LoggerFactory.getLogger(ProbeGUI.class);

	// ugly, but useful
	public static Stage primaryStage;

	private static BorderPane rootPane;

	private boolean firstTime;
	private TrayIcon trayIcon;

	public static ResourceBundle rb;

	public static void main(String[] args) {
		rb = ResourceBundle.getBundle("messageCodes", new java.util.Locale("en"));
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.DEBUG);
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Platform.setImplicitExit(false);

		firstTime = true;

		ProbeGUI.primaryStage = primaryStage;

		primaryStage.setTitle(ConfigItems.PROBE_TITLE);
		primaryStage.getIcons().add(new Image(ProbeGUI.class.getResourceAsStream("/gui/images/logo.png")));

		// Remove window decoration
		primaryStage.initStyle(StageStyle.UNDECORATED);

		// prevent resizing of GUI Window
		primaryStage.setResizable(false);

		createTrayIcon(primaryStage);

		if (TimingUtils.netIsAvailable(ConfigItems.BASE_URL)) {
			ProbeConfiguration.setAPIAvailablitity(true);
			log.info("SERVER REACHABLE!");
		}
		CompanyLicenseObj license = checkIfLicenseExists();

		if (license != null) {

			Date expirationDate = ProbeUtils.convertStringtoDate(license.getExpirationDate());
			if (!isLicenseExpired(expirationDate)) {
				ProbeConfiguration.authKey = license.getAuthToken();
				ProbeConfiguration.probeId = license.getProbeId();
				ProbeConfiguration.licenseId = license.getLicenseId();

				if (!ProbeConfiguration.isAPIAvailable()) {
					ProbeConfiguration.isLicenseValid = true;
				}

				ProbeConfiguration.getInstance();

				if (ProbeConfiguration.isLicenseValid) {
					initRootPane();
				} else {
					initLicenseImportPane("Your license has expired! Please import the new one!");
				}

			} else {
				initLicenseImportPane("Your license has expired! Please import the new one!");
			}

		}

		else {
			initLicenseImportPane("");
		}
	}

	/**
	 * This function checks if the license exists and according to that sets the
	 * different view on the start
	 * 
	 * @return
	 */
	public CompanyLicenseObj checkIfLicenseExists() {
		// LicenseDaoImpl licenseDao = new LicenseDaoImpl();
		CompanyLicenseObj license = getLicenseFromDb();
		if (license != null) {
			if (license.isActivated()) {
				// String licenseExists = APIUtils.sendPostWithResponse(ConfigItems.license_api,
				// license.id);

				return license;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static CompanyLicenseObj getLicenseFromDb() {
		List<CompanyLicenseObj> licenses = DBUtil.getInstance().findAll(new CompanyLicenseObj());

		if (licenses.size() != 1) {
			return null;
		} else {
			return licenses.get(0);
		}

	}

	public static void initLicenseImportPane(String errorText) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ProbeGUI.class.getResource(ViewNavigator.LICENSE_VIEW));
		// final FXMLLoader fxmlLoader = new
		// FXMLLoader(getClass().getResource(ViewNavigator.LICENSE_VIEW));
		loader.getNamespace().put("labelText", errorText);
		Parent root = loader.load();
		Scene scene = new Scene(root);
		primaryStage.setTitle("Import your license");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * This function initializes the Root Pane, it is called after the user is
	 * successfully logged in
	 */

	public static void initRootPane() {
		try {
			primaryStage.close();
			FXMLLoader loader = new FXMLLoader();
			/*
			 * Creates the locale holder which allows to provide multi language messages.
			 */
			LocaleHolder.setLocale("en");
			loader.setLocation(ProbeGUI.class.getResource(ViewNavigator.MAIN));
			loader.setResources(ResourceBundle.getBundle("messageCodes", LocaleHolder.getLocale()));

			rootPane = (BorderPane) loader.load();

			ViewNavigator.setMainController(loader.getController());

			Scene scene = new Scene(rootPane);
			rootPane.setBorder(
					new Border(new BorderStroke(Color.AQUAMARINE, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			primaryStage.setScene(scene);
			primaryStage.show();

			ProbeWorkerThread workerThread = new ProbeWorkerThread();
			workerThread.run();

		} catch (IOException e) {
			log.error("Could not initialize GUI Root Pane. Reason:", e);
		}

	}

	/**
	 * Returns the primary stage.
	 *
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void createTrayIcon(final Stage stage) {
		if (SystemTray.isSupported()) {
			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();
			// load an image
			java.awt.Image image = null;
			try {
				image = ImageIO.read(ProbeGUI.class.getResourceAsStream("/gui/images/logo-16x16.png"));
			} catch (IOException ex) {
				System.out.println(ex);
			}

			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent t) {
					hide(stage);
				}
			});
			stage.iconifiedProperty().addListener((observable, oldValue, iconified) -> {
				if (iconified) {
					// do something on minimize window
					stage.hide();
				} else {
					// do something on restore window
					// stage.show();
				}
			});

			// create a action listener to listen for default action executed on
			// the tray icon
			final ActionListener closeListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.exit(0);
				}
			};

			ActionListener showListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							stage.show();
						}
					});
				}
			};
			// create a popup menu
			PopupMenu popup = new PopupMenu();

			MenuItem showItem = new MenuItem("Show");
			showItem.addActionListener(showListener);
			popup.add(showItem);

			MenuItem closeItem = new MenuItem("Close");
			closeItem.addActionListener(closeListener);
			popup.add(closeItem);
			/// ... add other items
			// construct a TrayIcon
			trayIcon = new TrayIcon(image, "Simple2Secure Probe", popup);
			// set the TrayIcon properties
			trayIcon.addActionListener(showListener);
			// ...
			// add the tray image
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}
			// ...
		}
	}

	public void showProgramIsMinimizedMsg() {
		if (firstTime) {
			trayIcon.displayMessage("Simple2secure is minimized", "Double click to maximize", TrayIcon.MessageType.INFO);
			firstTime = false;
		}
	}

	private void hide(final Stage stage) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (SystemTray.isSupported()) {
					stage.hide();
					showProgramIsMinimizedMsg();
				} else {
					System.exit(0);
				}
			}
		});
	}

	private boolean isLicenseExpired(Date expirationDate) {
		return System.currentTimeMillis() > expirationDate.getTime();
	}
}
