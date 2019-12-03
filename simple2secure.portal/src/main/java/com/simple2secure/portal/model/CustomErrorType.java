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
package com.simple2secure.portal.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.MultiValueMap;

public class CustomErrorType implements MultiValueMap<String, String> {

	private String error;

	public CustomErrorType(String errorMessage) {
		error = errorMessage;
	}

	public String getErrorMessage() {
		return error;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Entry<String, List<String>>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> put(String key, List<String> value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends List<String>> m) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<List<String>> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFirst(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(String key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAll(String key, List<? extends String> values) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAll(MultiValueMap<String, String> values) {
		// TODO Auto-generated method stub

	}

	@Override
	public void set(String key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAll(Map<String, String> values) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> toSingleValueMap() {
		// TODO Auto-generated method stub
		return null;
	}

}
