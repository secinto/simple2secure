package com.simple2secure.portal.security.tls;

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

	private String acceptedSerialNumber;
	private X509TrustManager sunJsseX509TrustManager;
	private List<X509Certificate> trustedIssuers;

	public Simple2SecureTrustManager(String acceptedSerialNumber, X509TrustManager sunJsseX509TrustManager) {
		trustedIssuers = new ArrayList<X509Certificate>();
		this.acceptedSerialNumber = acceptedSerialNumber;
		this.sunJsseX509TrustManager = sunJsseX509TrustManager;
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		for (X509Certificate cert : chain) {
			log.debug("Client Certificate Chain {}", cert.getSubjectX500Principal().toString());
		}
		if (sunJsseX509TrustManager != null)
			sunJsseX509TrustManager.checkClientTrusted(chain, authType);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		try {
			if (sunJsseX509TrustManager != null)
				sunJsseX509TrustManager.checkServerTrusted(chain, authType);
		} catch (CertificateException excep) {
			for (X509Certificate cert : chain) {
				log.debug("Client Certificate Chain {}", cert.getSubjectX500Principal().toString());
				if (cert.getSerialNumber().toString().equals(acceptedSerialNumber)) {
					if (!trustedIssuers.contains(cert)) {
						trustedIssuers.add(cert);
					}
				} else {
					throw new CertificateException(
							"Not trusted certificate found. Currently only trusting certificate with serial " + acceptedSerialNumber);
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

}
