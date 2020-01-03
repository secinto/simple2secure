package com.simple2secure.commons.security;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustAllHostnameVerifier implements HostnameVerifier {

	private static Logger log = LoggerFactory.getLogger(TrustAllHostnameVerifier.class);

	@Override
	public boolean verify(String hostname, SSLSession session) {
		log.debug("Verifying hostname {} with true", hostname);
		return true;
	}

}