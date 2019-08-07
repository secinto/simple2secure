package com.simple2secure.test.portal.utils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TLSConfig {
	private static Logger log = LoggerFactory.getLogger(TLSConfig.class);

	public static void sslContextConfiguration() {
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { new AcceptAllTrustManager() }, null);
			SSLContext.setDefault(sslContext);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
			log.error("Couldn't initialize SSLContext for TLS. Reason {}", e.getLocalizedMessage());
		}
	}
}
