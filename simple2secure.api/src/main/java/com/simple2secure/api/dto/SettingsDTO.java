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

package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.TestMacro;
import com.simple2secure.api.model.Widget;

public class SettingsDTO {

	private Settings settings;

	private List<LicensePlan> licensePlan;

	private List<TestMacro> testMacroList;

	private List<Widget> widgetList;

	private List<String> widgetApis;

	public SettingsDTO(Settings settings, List<LicensePlan> licensePlan, List<TestMacro> testMacroList, List<Widget> widgetList,
			List<String> widgetApis) {
		this.settings = settings;
		this.licensePlan = licensePlan;
		this.testMacroList = testMacroList;
		this.widgetList = widgetList;
		this.widgetApis = widgetApis;
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

	public List<Widget> getWidgetList() {
		return widgetList;
	}

	public void setWidgetList(List<Widget> widgetList) {
		this.widgetList = widgetList;
	}

	public List<String> getWidgetApis() {
		return widgetApis;
	}

	public void setWidgetApis(List<String> widgetApis) {
		this.widgetApis = widgetApis;
	}
}
