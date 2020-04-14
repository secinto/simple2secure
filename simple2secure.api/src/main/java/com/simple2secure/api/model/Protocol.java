package com.simple2secure.api.model;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.StringUtils;

@Embeddable
public enum Protocol {
	HTTP, HTTPS, BlueCove, UNKNOWN;

	public static Protocol getProtocol(String protocol) {
		switch (StringUtils.upperCase(protocol)) {
		case "HTTP":
			return Protocol.HTTP;
		case "HTTPS":
			return Protocol.HTTPS;
		case "BlueCove":
			return Protocol.BlueCove;
		default:
			return Protocol.UNKNOWN;
		}
	}
}
