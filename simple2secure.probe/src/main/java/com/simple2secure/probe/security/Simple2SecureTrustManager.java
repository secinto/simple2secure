package com.simple2secure.probe.security;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simple2SecureTrustManager implements X509TrustManager {

	private static Logger log = LoggerFactory.getLogger(Simple2SecureTrustManager.class);

	private String[] acceptedSerialNumbers;
	private X509TrustManager sunJsseX509TrustManager;
	private List<X509Certificate> trustedIssuers;

	public Simple2SecureTrustManager(String[] acceptedSerialNumbers, X509TrustManager sunJsseX509TrustManager) {
		trustedIssuers = new ArrayList<X509Certificate>();
		this.acceptedSerialNumbers = acceptedSerialNumbers;
		this.sunJsseX509TrustManager = sunJsseX509TrustManager;
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		for (X509Certificate cert : chain) {
			log.debug("Client Certificate Chain {}", cert.getSubjectX500Principal().toString());
			log.debug("Client Certificate Serial {}", cert.getSerialNumber());
		}
		if (sunJsseX509TrustManager != null) {
			sunJsseX509TrustManager.checkClientTrusted(chain, authType);
		}
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		try {
			if (sunJsseX509TrustManager != null) {
				sunJsseX509TrustManager.checkServerTrusted(chain, authType);
			}
		} catch (CertificateException excep) {
			for (X509Certificate cert : chain) {
				log.debug("Client Certificate Chain {}", cert.getSubjectX500Principal().toString());
				if (stringContainsItemFromList(cert.getSerialNumber().toString(), acceptedSerialNumbers)) {
					if (!trustedIssuers.contains(cert)) {
						trustedIssuers.add(cert);
					}
				} else {
					throw new CertificateException(
							"Not trusted certificate found. Currently only trusting certificate with serial " + acceptedSerialNumbers);
				}
			}
		}

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		ArrayList<X509Certificate> trustedCerts = new ArrayList<X509Certificate>(Arrays.asList(sunJsseX509TrustManager.getAcceptedIssuers()));
		trustedCerts.addAll(trustedIssuers);
		return trustedCerts.toArray(new X509Certificate[0]);
	}

	public static boolean stringContainsItemFromList(String inputStr, String[] items) {
		return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
	}

}
