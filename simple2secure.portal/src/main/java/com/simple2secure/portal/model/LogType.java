package com.simple2secure.portal.model;

public enum LogType {
	TRACE("trace", 0), DEBUG("debug", 1), INFO("info", 2), WARNING("warning", 3), ERROR("error", 4), SEVERE("severe", 5), CRITICAL("critical",
			6);

	private String name;
	private int level;

	private LogType(String name, int level) {
		this.name = name;
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}
}
