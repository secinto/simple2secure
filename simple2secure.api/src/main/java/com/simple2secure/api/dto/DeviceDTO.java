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

import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.TestObjWeb;
import com.simple2secure.api.model.TestSequence;

public class DeviceDTO {

	private Device device;

	private List<TestObjWeb> test;

	private List<TestSequence> test_sequence;
	public DeviceDTO() {
	}

	public DeviceDTO(Device device, List<TestObjWeb> test, List<TestSequence> test_sequence) {
		this.device = device;
		this.test = test;
		this.test_sequence = test_sequence;
	}

	public Device getPod() {
		return device;
	}

	public void setPod(Device pod) {
		this.device = pod;
	}

	public List<TestObjWeb> getTest() {
		return test;
	}

	public void setTest(List<TestObjWeb> test) {
		this.test = test;
	}

	public List<TestSequence> getTest_sequence() {
		return test_sequence;
	}

	public void setTest_sequence(List<TestSequence> test_sequence) {
		this.test_sequence = test_sequence;
	}
}
