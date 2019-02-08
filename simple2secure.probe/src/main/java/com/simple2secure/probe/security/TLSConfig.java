package com.simple2secure.probe.security;

import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TLSConfig {

	private static Logger log = LoggerFactory.getLogger(TLSConfig.class);

	public static void initializeTLSConfiguration(String[] acceptedSerialNumbers) {
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
