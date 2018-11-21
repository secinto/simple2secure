package com.simple2secure.probe.scheduler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.gui.ProbeGUI;

public class ConfigScheduler extends TimerTask implements PropertyChangeListener {

	private static Logger log = LoggerFactory.getLogger(ConfigScheduler.class);
	
	private boolean probeIsLicenseValid = false;
	public boolean isProbeIsLicenseValid() {
		return probeIsLicenseValid;
	}
	public void setProbeIsLicenseValid(boolean probeIsLicenseValid) {
		this.probeIsLicenseValid = probeIsLicenseValid;
	}

	private boolean probeIsApiAvailable = false;
	public boolean isProbeIsApiAvailable() {
		return probeIsApiAvailable;
	}
	public void setProbeIsApiAvailable(boolean probeIsApiAvailable) {
		this.probeIsApiAvailable = probeIsApiAvailable;
	}

	private boolean probeIsGuiRunning = false;
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
		if (probeIsLicenseValid) {
			checkConfiguration();
		}
	}

	private void checkConfiguration() {
		log.info("Checking for the new configuration...");
		//ProbeConfiguration.getInstance().checkConfig();
		if(!probeIsLicenseValid) {
			if(probeIsGuiRunning) {
				ProbeWorkerThread.stopTimerTasks();
				try {
					ProbeGUI.initLicenseImportPane("Your license has expired! Please import the new one!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//TODO - find a better solution
			//Stop all timer tasks including Network Monitor and OSQuery
			//Change to the license view
			//ProbeWorkerThread.stopTimerTasks();		
//			Platform.runLater(() -> {
//				try {
//					ProbeGUI.initLicenseImportPane("Your license has expired! Please import the new one!");
//				} catch (IOException e) {
//					log.error("Error {}", e.getMessage());
//				}
//			});				
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if(arg0.getPropertyName().equals("isApiAvailable"))
			setProbeIsApiAvailable((boolean) arg0.getNewValue());
		if(arg0.getPropertyName().equals("isLicenseValid"))
			setProbeIsLicenseValid((boolean) arg0.getNewValue());
		if(arg0.getPropertyName().equals("isGuiRunning"))
			setProbeIsGuiRunning((boolean) arg0.getNewValue());
	}
}
