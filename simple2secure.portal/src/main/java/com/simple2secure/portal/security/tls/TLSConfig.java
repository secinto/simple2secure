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
package com.simple2secure.portal.security.tls;

import java.security.KeyStore;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TLSConfig {

	private static Logger log = LoggerFactory.getLogger(TLSConfig.class);

	@Value("${server.certificate.serial}")
	private String[] acceptedSerialNumbers;

	@Value("${server.ssl.trust-store}")
	private String truststore;
	@Value("${server.ssl.trust-store-password}")
	private String truststorePassword;

	@PostConstruct
	public void sslContextConfiguration() {
		X509TrustManager sunJsseX509TrustManager = null;
		try {

			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

			trustManagerFactory.init((KeyStore) null);

			TrustManager tms[] = trustManagerFactory.getTrustManagers();
			for (int i = 0; i < tms.length; i++) {
				if (tms[i] instanceof X509TrustManager) {
					sunJsseX509TrustManager = (X509TrustManager) tms[i];
				}
			}
		} catch (Exception e) {
			log.error("Couldn't find default TrustManager, go on without it. Reason {}", e.getLocalizedMessage());
		}

		try {

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { new Simple2SecureTrustManager(acceptedSerialNumbers, sunJsseX509TrustManager) }, null);
			SSLContext.setDefault(sslContext);
		} catch (Exception e) {
			log.error("Couldn't initialize SSLContext for TLS. Reason {}", e.getLocalizedMessage());
		}
	}
}
