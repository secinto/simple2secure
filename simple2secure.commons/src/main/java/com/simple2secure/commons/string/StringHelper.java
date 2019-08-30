/**
 *********************************************************************
 *
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
package com.simple2secure.commons.string;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * String utility class
 *
 * @author dferbas
 */
public final class StringHelper {

	private static final Logger log = LoggerFactory.getLogger(StringHelper.class);
	private static StringHelper instance = new StringHelper();

	/**
	 * private constructor
	 */
	private StringHelper() {
	}

	/**
	 * Get the instance of the StringHelper that is only nice for scripting.
	 *
	 * @return
	 */
	public static StringHelper getInstance() {
		return instance;
	}

	/**
	 * Returns if string is null or empty.<br>
	 * YOU SHOULD USE com.google.common.base.Strings.isNullOrEmpty(String)
	 *
	 * @param str
	 * @return
	 */
	public boolean isNullOrEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * Returns if string is NOT null and empty<br>
	 *
	 * @param str
	 * @return
	 */
	public boolean notNullOrEmpty(String str) {
		return !isNullOrEmpty(str);
	}

	/**
	 * Return if string is not empty after {@link String#trim()}
	 *
	 * @param str
	 * @return
	 */
	public boolean textHasContent(String str) {
		return notNullOrEmpty(str) && notNullOrEmpty(str.trim());
	}

	/**
	 * resolves a string with pattern. (e.g. %test1%)
	 *
	 * @param values
	 *          value map which contains string castable values
	 * @param separator
	 *          pattern separator which surrounds the string to replace
	 * @param stringPattern
	 *          string which contains the replacement text
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String resolveString(Map values, String separator, String stringPattern) {
		if (isNullOrEmpty(stringPattern) || values == null) {
			return stringPattern;
		}

		String resolvedString = stringPattern;

		int lastIndex = 0;
		boolean oneIndexSet = false;

		while (lastIndex != -1) {
			int index = stringPattern.indexOf(separator, lastIndex);
			if (oneIndexSet) {
				String replacementString = stringPattern.substring(lastIndex, index);
				String value = (String) values.get(replacementString);
				if (value == null) {
					log.warn("Cannot resolve right string, because key '{}' not found or null", replacementString); //$NON-NLS-1$
				} else {
					StringBuilder sb = new StringBuilder(separator);
					sb.append(replacementString);
					sb.append(separator);
					resolvedString = resolvedString.replace(sb.toString(), value);
				}
			}

			oneIndexSet = !oneIndexSet;
			lastIndex = index;
			if (lastIndex != -1) {
				lastIndex++;
			}

		}

		return resolvedString;
	}

	/**
	 * checks if a string contains only numeric characters
	 *
	 * @param s
	 * @return
	 */
	public boolean isNumeric(String s) {
		if (s == null) {
			return false;
		}

		return Pattern.matches("\\d+(\\.\\d+)?", s); //$NON-NLS-1$
	}

	public boolean isValue(String s) {
		if (s == null) {
			return false;
		}
		String value = s.replace("(", "").replace(")", "");

		if (value.endsWith("U") || value.endsWith("u")) {
			value = value.replace("U", "").replace("u", "");
		}

		if (value.contains("0x")) {
			value = value.replace("0x", "");
			return Pattern.matches("[0-9ABCDEF]+", value);
		}
		return Pattern.matches("\\d+(\\.\\d+)?", value); //$NON-NLS-1$
	}

	/**
	 * Remove all whitespace characters from a string and return the resulting string.
	 *
	 * @param s
	 * @return
	 */
	public String stripWhiteSpaces(String s) {
		StringBuilder sb = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (c != ' ' && c != '\r' && c != '\n' && c != '\t') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Tests if one string {@code s} contains another {@code toFind} ignoring the case.
	 *
	 * @param s
	 * @param toFind
	 * @return
	 */
	public boolean containsIgnoreCase(String s, String toFind) {
		if (s == null && toFind == null) {
			return true;
		}
		if (s == null || toFind == null) {
			return false;
		}
		return s.toLowerCase().contains(toFind.toLowerCase());
	}

	/**
	 * Helper for default assignment. Returns {@code stringToTest} if not null or empty, {@code defaultValue} otherwise.
	 *
	 * @param stringToTest
	 * @param defaultValue
	 * @return
	 */
	public String getIfNotNullorEmpty(String stringToTest, String defaultValue) {
		if (!Strings.isNullOrEmpty(stringToTest)) {
			return stringToTest;
		}
		return defaultValue;
	}

	/**
	 * Helper for default assignment. Returns {@code stringToTest} if not null or empty, {@code null} otherwise.
	 *
	 * @param stringToTest
	 * @return
	 */
	public String getIfNotNullorEmpty(String stringToTest) {
		return getIfNotNullorEmpty(stringToTest, null);
	}

	/**
	 * trims characters at the beginning and the end from a given string
	 *
	 * @param source
	 * @param sequence
	 *          the given sequence that will be trimmed
	 * @return
	 */
	public String trim(String source, String sequence) {
		String string = source;

		if (Strings.isNullOrEmpty(string)) {
			return string;
		}

		while (string.startsWith(sequence)) {
			string = string.substring(sequence.length(), string.length());
		}

		while (string.endsWith(sequence)) {
			string = string.substring(0, string.length() - sequence.length());
		}

		return string;
	}

	/**
	 * Convert given source to lower case and replaces all german umlauts with ae, oe and ue as well as &#223; to ss
	 *
	 * @param source
	 *          may be null
	 * @return
	 */
	public String convert(String source) {
		if (source == null) {
			return source;
		}
		source = source.toLowerCase().replaceAll("\u00E4", "ae").replaceAll("\u00F6", "oe").replaceAll("\u00FC", "ue") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				.replaceAll("\u00df", "ss"); //$NON-NLS-1$ //$NON-NLS-2$
		source = StringUtils.stripAccents(source);
		return source;
	}

	private final String OBJECT_FIELD_SEPARATOR = ", "; //$NON-NLS-1$

	/**
	 * "converts" an object and its fields as string (using reflection)<br>
	 * only use this method if commons-lang ReflectionToStringBuilder is not available
	 *
	 * @param obj
	 * @return
	 */
	public String objectToString(Object obj) {
		StringBuilder sb = new StringBuilder(obj.getClass().getSimpleName());
		sb.append("["); //$NON-NLS-1$
		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = obj.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			try {
				sb.append(field.getName());
				sb.append("="); //$NON-NLS-1$
				boolean accessible = field.isAccessible();
				field.setAccessible(true);
				sb.append(field.get(obj));
				field.setAccessible(accessible);
			} catch (IllegalAccessException ex) {
				log.error("Unable to create string", ex); //$NON-NLS-1$
			}
			sb.append(OBJECT_FIELD_SEPARATOR);
		}
		int lastIndex = sb.lastIndexOf(OBJECT_FIELD_SEPARATOR);
		if (lastIndex > 0) {
			sb.replace(lastIndex, lastIndex + OBJECT_FIELD_SEPARATOR.length(), ""); //$NON-NLS-1$
		}
		sb.append("]"); //$NON-NLS-1$
		return sb.toString();
	}

	/**
	 * replaces the last instance of this string
	 *
	 * @param string
	 * @param toReplace
	 * @param replacement
	 * @return
	 */
	public String replaceLast(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length(), string.length());
		}
		return string;
	}

	public boolean isUpperCase(String data, boolean ignoreSpecialCharacters) {
		for (int i = 0; i < data.length(); i++) {
			if (ignoreSpecialCharacters && !Character.isLetter(data.charAt(i))) {
				continue;
			}

			if (Character.isLowerCase(data.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public boolean startsWithUpperCase(String data) {
		if (Character.isLowerCase(data.charAt(0))) {
			return false;
		}
		return true;
	}
}
