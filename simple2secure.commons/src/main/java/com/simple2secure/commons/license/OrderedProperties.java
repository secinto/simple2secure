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
package com.simple2secure.commons.license;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class OrderedProperties extends Properties {

	/**
	 *
	 */
	private static final long serialVersionUID = 1608473191644819426L;

	private final Map<Object, Object> orderedMap = new LinkedHashMap<>();

	public OrderedProperties() {

	}

	public OrderedProperties(Properties properties) {
		properties.forEach((key, value) -> orderedMap.put(key, value));
	}

	@Override
	public Object get(Object key) {
		return orderedMap.get(key);
	}

	@Override
	public Object put(Object key, Object value) {
		return orderedMap.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return orderedMap.remove(key);
	}

	@Override
	public void clear() {
		orderedMap.clear();
	}

	@Override
	public Enumeration<Object> keys() {
		return Collections.enumeration(orderedMap.keySet());
	}

	@Override
	public Enumeration<Object> elements() {
		return Collections.enumeration(orderedMap.values());
	}

	@Override
	public Set<Map.Entry<Object, Object>> entrySet() {
		return orderedMap.entrySet();
	}

	@Override
	public int size() {
		return orderedMap.size();
	}

	@Override
	public String getProperty(String key) {
		return (String) orderedMap.get(key);
	}

	@Override
	public synchronized boolean containsKey(Object key) {
		return orderedMap.containsKey(key);
	}

	@Override
	public String toString() {
		return orderedMap.toString();
	}
}
