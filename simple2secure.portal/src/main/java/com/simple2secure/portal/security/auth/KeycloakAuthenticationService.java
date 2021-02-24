package com.simple2secure.portal.security.auth;

import java.util.HashMap;
import java.util.Map;

import org.keycloak.adapters.HttpClientBuilder;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.util.Http;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.simple2secure.api.model.AuthToken;
import com.simple2secure.api.model.KeycloakError;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.portal.exceptions.KeycloakRequestException;

@Service
public class KeycloakAuthenticationService {

	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;

	/**
	 * This function returns new access and refresh tokens using the client credentials.
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	public AuthToken getNewTokenFromCredentials(String username, String password) {

		Configuration configuration = getConfiguration();

		AuthzClient authzClient = AuthzClient.create(configuration);

		try {
			AccessTokenResponse accessTokenResponse = authzClient.obtainAccessToken(username, password);
			AuthToken authToken = new AuthToken(accessTokenResponse.getToken(), accessTokenResponse.getRefreshToken());
			return authToken;
		} catch (HttpResponseException e) {
			String except = e.toString();
			KeycloakError error = JSONUtils.fromString(except.substring(except.indexOf("{")), KeycloakError.class);
			if (error != null) {
				throw new KeycloakRequestException(error.getError_description(), e);
			}
			throw new KeycloakRequestException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * This functions retrieves new auth token using the provided refresh token.
	 *
	 * @param refreshToken
	 * @return
	 */
	public AuthToken getNewTokenFromRefresh(String refreshToken) {

		Configuration configuration = getConfiguration();
		String url = authServerUrl + "/realms/" + StaticConfigItems.REALM_S2S_DEVELOPMENT + "/protocol/openid-connect/token";
		Http http = new Http(configuration, (params, headers) -> {
		});

		AccessTokenResponse tokenResponse = http.<AccessTokenResponse>post(url).authentication().client().form()
				.param("grant_type", "refresh_token").param("refresh_token", refreshToken).param("client_id", StaticConfigItems.CLIENT_S2S_WEB)
				.param("client_secret", (String) configuration.getCredentials().get("secret")).response().json(AccessTokenResponse.class).execute();

		AuthToken authToken = new AuthToken(tokenResponse.getToken(), tokenResponse.getRefreshToken());

		return authToken;

	}

	/**
	 * This functions returns the configuration used to communicate with the keycloak.
	 *
	 * @return
	 */
	private Configuration getConfiguration() {
		Map<String, Object> clientCredentials = new HashMap<>();
		clientCredentials.put("secret", StaticConfigItems.CLIENT_S2S_TOKEN);
		Configuration configuration = new Configuration(authServerUrl, StaticConfigItems.REALM_S2S_DEVELOPMENT,
				StaticConfigItems.CLIENT_S2S_WEB, clientCredentials, new HttpClientBuilder().disableTrustManager().connectionPoolSize(10).build());
		return configuration;
	}

}
