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
package com.simple2secure.probe.scheduler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.gui.ProbeGUI;

public class ConfigScheduler extends TimerTask implements PropertyChangeListener {

	private static Logger log = LoggerFactory.getLogger(ConfigScheduler.class);

	private boolean probeIsLicenseValid = false;
	private boolean probeIsGuiRunning = false;
	private boolean probeIsApiAvailable = false;

	public boolean isProbeIsLicenseValid() {
		return probeIsLicenseValid;
	}

	public void setProbeIsLicenseValid(boolean probeIsLicenseValid) {
		this.probeIsLicenseValid = probeIsLicenseValid;
	}

	public boolean isProbeIsApiAvailable() {
		return probeIsApiAvailable;
	}

	public void setProbeIsApiAvailable(boolean probeIsApiAvailable) {
		this.probeIsApiAvailable = probeIsApiAvailable;
	}

	public boolean isProbeIsGuiRunning() {
		return probeIsGuiRunning;
	}

	private void setProbeIsGuiRunning(boolean probeIsGuiRunning) {
		this.probeIsGuiRunning = probeIsGuiRunning;
	}

	public ConfigScheduler() {
	}

	@Override
	public void run() {
		checkConfiguration();
	}

	private void checkConfiguration() {
		log.info("Checking for the new configuration...");
		ProbeConfiguration.getInstance().checkAndUpdateConfigFromAPI();
		if (!probeIsLicenseValid) {
			if (probeIsGuiRunning) {
				try {
					ProbeGUI.initLicenseImportPane("Your license has expired! Please import the new one!");
				} catch (IOException e) {
					log.error("Checking configuration was not successful. Reason {}", e);
				}
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals("isApiAvailable")) {
			setProbeIsApiAvailable((boolean) event.getNewValue());
		}
		if (event.getPropertyName().equals("isLicenseValid")) {
			setProbeIsLicenseValid((boolean) event.getNewValue());
		}
		if (event.getPropertyName().equals("isGuiRunning")) {
			setProbeIsGuiRunning((boolean) event.getNewValue());
		}
	}
}
