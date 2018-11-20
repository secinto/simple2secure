package com.simple2secure.probe.scheduler;

import java.io.IOException;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.gui.ProbeGUI;

import javafx.application.Platform;

public class ConfigScheduler extends TimerTask {

	private static Logger log = LoggerFactory.getLogger(ConfigScheduler.class);

	public ConfigScheduler() {
	}

	@Override
	public void run() {
		if (ProbeConfiguration.isAPIAvailable()) {
			checkConfiguration();
		}
	}

	private void checkConfiguration() {
		log.info("Checking for the new configuration...");
		ProbeConfiguration.getInstance().checkConfig();
		
		if(!ProbeConfiguration.isLicenseValid) {
			//TODO - find a better solution
			//Stop all timer tasks including Network Monitor and OSQuery
			//Change to the license view
			ProbeWorkerThread.stopTimerTasks();		
//			Platform.runLater(() -> {
//				try {
//					ProbeGUI.initLicenseImportPane("Your license has expired! Please import the new one!");
//				} catch (IOException e) {
//					log.error("Error {}", e.getMessage());
//				}
//			});				
		}
	}

}
