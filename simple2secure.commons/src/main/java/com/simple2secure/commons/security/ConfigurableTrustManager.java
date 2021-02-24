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
package com.simple2secure.commons.security;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurableTrustManager implements X509TrustManager {

	private static Logger log = LoggerFactory.getLogger(ConfigurableTrustManager.class);

	private String[] acceptedSerialNumbers;
	private X509TrustManager sunJsseX509TrustManager;
	private List<X509Certificate> trustedIssuers;

	public ConfigurableTrustManager(String[] acceptedSerialNumbers, X509TrustManager sunJsseX509TrustManager) {
		trustedIssuers = new ArrayList<>();
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
				for (X509Certificate cert : chain) {
					log.debug("Client Certificate Chain {}", cert.getSubjectX500Principal().toString());
					log.debug("Client Certificate Serial Number {}", cert.getSerialNumber().toString());
					log.debug("Client Certificate Issuer DN {}", cert.getIssuerX500Principal());
					log.debug("Client Certificate Subject DN {}", cert.getSubjectX500Principal());
					if (stringContainsItemFromList(cert.getSerialNumber().toString(), acceptedSerialNumbers)) {
						if (!trustedIssuers.contains(cert)) {
							trustedIssuers.add(cert);
						}
					} else {
						log.error("Client certificate {} with serial number {} not found in list of trusted certificates!",
								cert.getSubjectX500Principal().toString(), cert.getSerialNumber().toString());
						throw new CertificateException(
								"Not trusted certificate found. Currently only trusting certificate with serial " + acceptedSerialNumbers);
					}
				}
			}
		} catch (CertificateException excep) {
			for (X509Certificate cert : chain) {
				if (stringContainsItemFromList(cert.getSerialNumber().toString(), acceptedSerialNumbers)) {
					if (!trustedIssuers.contains(cert)) {
						trustedIssuers.add(cert);
					}
				} else {
					log.error("Client certificate {} with serial number {} not found in list of trusted certificates!",
							cert.getSubjectX500Principal().toString(), cert.getSerialNumber().toString());
					throw new CertificateException(
							"Not trusted certificate found. Currently only trusting certificate with serial " + acceptedSerialNumbers);
				}
			}
		}

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		ArrayList<X509Certificate> trustedCerts = new ArrayList<>(Arrays.asList(sunJsseX509TrustManager.getAcceptedIssuers()));
		trustedCerts.addAll(trustedIssuers);
		return trustedCerts.toArray(new X509Certificate[0]);
	}

	public static boolean stringContainsItemFromList(String inputStr, String[] items) {
		return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
	}

}
