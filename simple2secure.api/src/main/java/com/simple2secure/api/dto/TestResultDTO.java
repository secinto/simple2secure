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

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.TestResult;

public class TestResultDTO {

	private TestResult testResult;

	private CompanyGroup group;

	public TestResultDTO(TestResult testResult, CompanyGroup group) {
		this.testResult = testResult;
		this.group = group;
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

	public CompanyGroup getGroup() {
		return group;
	}

	public void setGroup(CompanyGroup group) {
		this.group = group;
	}

}
