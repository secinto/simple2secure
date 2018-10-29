package com.simple2secure.probe.gui.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.gui.ProbeGUI;
import com.simple2secure.probe.gui.model.WindowButtons;
import com.simple2secure.probe.logging.GUIAppender;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ProbeController {

	private static final String SHOW_LOGS = "Show logs";
	private static final String HIDE_LOGS = "Hide logs";

	@FXML
	private VBox vbButtons;
	// private ToggleGroup tbgButtons;

	@FXML
	private BorderPane rootPane;

	@FXML
	private BorderPane bpView;

	@FXML
	private HBox hbPath;

	public void setView(Node n) {
		bpView.setCenter(n);
	}

	public Node getView() {
		return bpView.getCenter();
	}

	// needed for drag&drop of the window
	private double xOffset = 0;
	private double yOffset = 0;

	private static Logger log = LoggerFactory.getLogger(ProbeController.class);

	public static ObservableList<String> infos = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		try {
			// ProbeConfiguration.getInstance();
		} catch (Exception e) {
			log.debug("Couldn't read in the Configuration file!", e);
			return;
		}

		initTitleBar();
	}

	/***
	 * Creates the title bar and adds buttons and listeners to it. Only outsourced
	 * for legibility
	 */
	private void initTitleBar() {
		// the following snippet creates a custom window bar
		ToolBar tool = new ToolBar();

		// Imageview for the Icon
		Stage s = ProbeGUI.primaryStage;
		ImageView iv;
		if (!s.getIcons().isEmpty()) {
			iv = new ImageView(s.getIcons().get(0));
			iv.setFitHeight(16);
			iv.setFitWidth(16);
		} else {
			iv = new ImageView();
		} /**/

		// label to display the application title
		Label l = new Label(StaticConfigItems.PROBE_TITLE);

		// the HBox is required to right-align the buttons.
		HBox h = new HBox();
		HBox.setHgrow(h, Priority.ALWAYS);
		tool.getItems().addAll(iv, l, h, new WindowButtons());

		// both these listeners are required for drag&drop of the window
		tool.setOnMousePressed(event -> {
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
		});
		tool.setOnMouseDragged(event -> {
			Stage stage = (Stage) ((ToolBar) event.getSource()).getScene().getWindow();
			stage.setX(event.getScreenX() - xOffset);
			stage.setY(event.getScreenY() - yOffset);
		});

		rootPane.setTop(tool);

		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		ObservableList<String> items = FXCollections.observableArrayList();
		ListView<String> list = new ListView<>();
		list.setVisible(false);

		Button logsBtn = new Button();
		logsBtn.setText(SHOW_LOGS);

		logsBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {

				if (logsBtn.getText().equals(SHOW_LOGS)) {
					logsBtn.setText(HIDE_LOGS);
					list.setVisible(true);
				} else {
					logsBtn.setText(SHOW_LOGS);
					list.setVisible(false);
				}
			}
		});

		// TextArea textArea = new TextArea();
		OutputStream os = new ListViewOutputStream(list, items);

		GUIAppender.setStaticOutputStream(os);

		Hyperlink hyperlink = new Hyperlink("More info...");
		TextFlow flow = new TextFlow(new Text(""), hyperlink);

		hyperlink.setOnMouseClicked(ev -> {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop()
							.browse(new URI(ProbeConfiguration.getInstance().getLoadedConfigItems().getBaseURLWeb()));
				} catch (IOException e) {
					log.error(e.getMessage());
				} catch (URISyntaxException e) {
					log.error(e.getMessage());
				}
			}
		});

		flow.setTextAlignment(TextAlignment.CENTER);
		vbox.getChildren().addAll(logsBtn, list, flow);
		rootPane.setCenter(vbox);

		HBox hb_footer = new HBox();
		hb_footer.setAlignment(Pos.BOTTOM_CENTER);
		Text footer = new Text(10, 100, "Simple2Secure v.0.0.1 - Registered to: " + ProbeConfiguration.licenseId);
		footer.setFont(Font.font(null, FontWeight.NORMAL, 8));
		footer.setTextAlignment(TextAlignment.CENTER);
		hb_footer.getChildren().add(footer);
		rootPane.setBottom(hb_footer);
	}

	private static class ListViewOutputStream extends OutputStream {

		private ListView<String> listview;
		ObservableList<String> items;
		String custom_item = "";

		public ListViewOutputStream(ListView<String> listview, ObservableList<String> items) {
			this.listview = listview;
			this.items = items;
		}

		@Override
		public void write(int b) throws IOException {
			String item = String.valueOf((char) b);

			if (item.equals("\n")) {
				if (custom_item.contains("\r")) {

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// custom_item = custom_item + item;
							custom_item = custom_item.replace("\r", "").replace("\n", "");
							if (!Strings.isNullOrEmpty(custom_item)) {
								items.add(custom_item);
								listview.setItems(items);
								custom_item = "";
							}

						}
					});

				} else {
					custom_item = custom_item + item;
				}
			} else {
				custom_item = custom_item + item;
			}
		}
	}
}
