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
package com.simple2secure.api.model;

public class TestDefinition {
	private String description;
	private String version;
	private TestStep precondition;
	private TestStep step;
	private TestStep postcondition;

	public TestDefinition() {

	}

	public TestDefinition(String description, String version, TestStep precondition, TestStep step, TestStep postcondition) {
		super();
		this.description = description;
		this.version = version;
		this.precondition = precondition;
		this.step = step;
		this.postcondition = postcondition;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public TestStep getPrecondition() {
		return precondition;
	}

	public void setPrecondition(TestStep precondition) {
		this.precondition = precondition;
	}

	public TestStep getStep() {
		return step;
	}

	public void setStep(TestStep step) {
		this.step = step;
	}

	public TestStep getPostcondition() {
		return postcondition;
	}

	public void setPostcondition(TestStep postcondition) {
		this.postcondition = postcondition;
	}

}
