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

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

	private List<?> object = new ArrayList<>();
	private String clazz;

	public SearchResult() {
		// TODO Auto-generated constructor stub
	}

	public SearchResult(List<?> object, String clazz) {
		this.object = object;
		this.clazz = clazz;
	}

	public List<?> getObject() {
		return object;
	}

	public void setObject(List<?> object) {
		this.object = object;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

}
