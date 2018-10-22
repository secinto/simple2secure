package com.simple2secure.probe.utils;

import java.util.Locale;

import com.simple2secure.commons.messages.Message;
import com.simple2secure.commons.messages.MessageCodeUtil;
import com.simple2secure.probe.gui.ProbeGUI;

public class LocaleHolder {
	private static Locale locale;

	public static void setLocale(Locale locale) {
		LocaleHolder.locale = locale;
		MessageCodeUtil.setThreadLocalLocale(locale.getLanguage());
	}

	public static void setLocale(String language, String region) {
		LocaleHolder.locale = new Locale(language, region);
		MessageCodeUtil.setThreadLocalLocale(locale.getLanguage());
	}

	public static void setLocale(String language) {
		LocaleHolder.locale = new Locale(language);
		MessageCodeUtil.setThreadLocalLocale(locale.getLanguage());
	}

	public static Locale getLocale() {
		return locale;
	}
	
	public static Message getMessage(String key) {
		return new Message(key, ProbeGUI.rb.getString(key));
	}
}
