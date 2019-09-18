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

import com.simple2secure.api.model.Pod;
import com.simple2secure.api.model.TestObjWeb;

public class PodDTO {

	private Pod pod;

	private List<TestObjWeb> test;

	public PodDTO() {
	}

	public PodDTO(Pod pod, List<TestObjWeb> test) {
		this.pod = pod;
		this.test = test;
	}

	public Pod getPod() {
		return pod;
	}

	public void setPod(Pod pod) {
		this.pod = pod;
	}

	public List<TestObjWeb> getTest() {
		return test;
	}

	public void setTest(List<TestObjWeb> test) {
		this.test = test;
	}

}