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
package com.simple2secure.probe.gui.model;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.gui.ProbeGUI;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class WindowButtons extends HBox {

	private static Logger log = LoggerFactory.getLogger(WindowButtons.class);

	/***
	 * This class defines which buttons are to be added to the title bar. As this is assumed to not change very much, it is implemented very
	 * static. If a button needs to be added, simply copy the snippet and change it accordingly. It also adds the S2S Probe icon to the system
	 * tray if the "minimize" button is clicked.
	 */
	public WindowButtons() {

		Image i = new Image(getClass().getResourceAsStream("/gui/images/minimize-24.png"), 16, 16, false, true);
		Button minBtn = new Button("", new ImageView(i));
		minBtn.setStyle("fx-border-style: none");

		minBtn.setOnAction(event -> {
			if (!SystemTray.isSupported()) {
				System.out.println("System tray is not supported !!! ");
			} else {
				TrayIcon trayIcon;

				ProbeGUI.primaryStage.hide();
				System.out.println("System tray is supported !!! ");
				SystemTray systemTray = SystemTray.getSystemTray();
				BufferedImage image = null;
				try {
					image = ImageIO.read(getClass().getResource("/gui/images/cmd-24.png"));
				} catch (IOException e1) {
					log.error(e1.getMessage());
				}
				PopupMenu trayPopupMenu = new PopupMenu();

				trayIcon = new TrayIcon(image, "S2S Probe", trayPopupMenu);

				MenuItem open = new MenuItem("Open");

				open.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								ProbeGUI.primaryStage.show();
							}

						});
					}
				});

				trayPopupMenu.add(open);

				MenuItem close = new MenuItem("Exit");
				close.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.print("Exiting S2S Probe\n");
						systemTray.remove(trayIcon);
						System.exit(0);
					}
				});
				trayPopupMenu.add(close);

				trayIcon.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								ProbeGUI.primaryStage.show();
							}

						});
					}
				});

				try {
					if (!checkIfTrayIconExists(trayIcon)) {
						systemTray.add(trayIcon);
						trayIcon.setImageAutoSize(true);
					}

				} catch (AWTException awtException) {
					awtException.printStackTrace();
				}
			}
		});

		minBtn.setPrefHeight(20);

		this.getChildren().addAll(minBtn);
	}

	/**
	 * This function checks if system tray icon is already added, so that no double entries exist.
	 *
	 * @param trayicon
	 * @return
	 */
	private boolean checkIfTrayIconExists(TrayIcon trayicon) {
		for (TrayIcon ti : SystemTray.getSystemTray().getTrayIcons()) {
			if (ti.getToolTip().equals(trayicon.getToolTip())) {
				return true;
			}
		}
		return false;
	}
}
