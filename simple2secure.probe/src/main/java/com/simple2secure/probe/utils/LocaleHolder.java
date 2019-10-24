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
package com.simple2secure.probe.utils;

import java.util.Locale;

import com.simple2secure.commons.messages.Message;
import com.simple2secure.probe.config.ProbeConfiguration;

public class LocaleHolder {
	private static Locale locale;

	public static void setLocale(Locale locale) {
		LocaleHolder.locale = locale;
	}

	public static void setLocale(String language, String region) {
		LocaleHolder.locale = new Locale(language, region);
	}

	public static void setLocale(String language) {
		LocaleHolder.locale = new Locale(language);
	}

	public static Locale getLocale() {
		return locale;
	}

	public static Message getMessage(String key) {
		return new Message(key, ProbeConfiguration.rb.getString(key));
	}
}
