/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.TestMacro;

public class SettingsDTO {

	private Settings settings;

	private List<LicensePlan> licensePlan;

	private List<TestMacro> testMacroList;

	public SettingsDTO(Settings settings, List<LicensePlan> licensePlan, List<TestMacro> testMacroList) {
		this.settings = settings;
		this.licensePlan = licensePlan;
		this.testMacroList = testMacroList;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public List<LicensePlan> getLicensePlan() {
		return licensePlan;
	}

	public void setLicensePlan(List<LicensePlan> licensePlan) {
		this.licensePlan = licensePlan;
	}

	public List<TestMacro> getTestMacroList() {
		return testMacroList;
	}

	public void setTestMacroList(List<TestMacro> testMacroList) {
		this.testMacroList = testMacroList;
	}
}
