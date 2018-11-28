
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
