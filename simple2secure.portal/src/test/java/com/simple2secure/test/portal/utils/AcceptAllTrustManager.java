package com.simple2secure.test.portal.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcceptAllTrustManager implements X509TrustManager {

	private static Logger log = LoggerFactory.getLogger(AcceptAllTrustManager.class);

	private List<X509Certificate> trustedIssuers;

	public AcceptAllTrustManager() {
		trustedIssuers = new ArrayList<X509Certificate>();
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		for (X509Certificate cert : chain) {
			log.debug("Client Certificate Chain {}", cert.getSubjectX500Principal().toString());
		}
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		for (X509Certificate cert : chain) {
			log.debug("Client Certificate Chain {}", cert.getSubjectX500Principal().toString());
			if (!trustedIssuers.contains(cert)) {
				trustedIssuers.add(cert);
			}
		}
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		ArrayList<X509Certificate> trustedCerts = new ArrayList<X509Certificate>(trustedIssuers);
		trustedCerts.addAll(trustedIssuers);
		return trustedCerts.toArray(new X509Certificate[0]);
	}

}
