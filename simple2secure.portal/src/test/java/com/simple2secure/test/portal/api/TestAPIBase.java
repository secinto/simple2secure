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
package com.simple2secure.test.portal.api;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.repository.UserRepository;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestAPIBase {

	private static Logger log = LoggerFactory.getLogger(TestAPIBase.class);

	@Autowired
	protected LoadedConfigItems loadedConfigItems;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SettingsRepository settingsRepository;

	@Autowired
	private ContextRepository contextRepository;

	@Autowired
	private ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	MongoTemplate mongoTemplate;

	@LocalServerPort
	protected int randomServerPort;

	private User adminUser;

	private User probeUser;

	private User superUser;

	private String accessTokenAdmin;

	private String accessTokenProbe;

	private String accessTokenSuperUser;

	HttpHeaders headers = new HttpHeaders();

	// TO-DO: We have to find a solution to delete database automatically after all tests are executed. Currently, the database is dropped
	// before test execution.

	@PostConstruct
	public void init() {

		// Drop the database before the tests
		mongoTemplate.getDb().drop();

		/*
		 * Update the port to the one randomly selected by the framework. Otherwise the URLs would be incorrect.
		 */
		loadedConfigItems.setBasePort(String.valueOf(randomServerPort));

		adminUser = createUser(UserRole.ADMIN);
		probeUser = createUser(UserRole.PROBE);
		superUser = createUser(UserRole.SUPERUSER);
		createSettings();
		setAccessTokenAdmin(obtainAccessToken(adminUser));
		setAccessTokenProbe(obtainAccessToken(probeUser));
		setAccessTokenSuperUser(obtainAccessToken(superUser));
	}

	/**
	 * Creates setting object in the database, so that accessToken can be obtained.
	 */
	private void createSettings() {
		Settings settings = new Settings();
		settings.setAccessTokenValidityTime(10);
		settings.setAccessTokenValidityUnit(TimeUnit.MINUTES);
		settings.setAccessTokenProbeRestValidityTime(10);
		settings.setAccessTokenProbeRestValidityTimeUnit(TimeUnit.MINUTES);
		settings.setAccessTokenProbeValidityTime(10);
		settings.setAccessTokenProbeValidityUnit(TimeUnit.MINUTES);
		settingsRepository.save(settings);
	}

	/**
	 * Creates user object in the database with the user role parameter which has been transferred, this object will be used in login
	 * procedure
	 *
	 * @return
	 */
	private User createUser(UserRole userRole) {
		User user = new User();
		String contextId = null;
		user.setEmail("probe@test.com");
		user.setPassword(passwordEncoder.encode("test"));
		user.setActivated(true);
		user.setActivationToken("54321");

		if (userRole.equals(UserRole.ADMIN)) {
			user.setEmail("testiing@test.com");
			user.setActivationToken("12345");
			contextId = createContext("Context 1");
		} else if (userRole.equals(UserRole.SUPERUSER)) {
			user.setEmail("superuser@test.com");
			user.setActivationToken("23145");
			contextId = createContext("Context 2");
		}

		userRepository.save(user);

		// We need an userId in some tests!!
		// TODO: Implement a function in MongoRepository which returns an object after saving

		user = userRepository.findByEmail(user.getEmail());

		ContextUserAuthentication contextUserAuth = new ContextUserAuthentication(user.getId(), contextId, userRole, true);
		contextUserAuthRepository.save(contextUserAuth);

		return user;
	}

	/**
	 * This function adds new admin group to the database, because it is needed for some tests.
	 *
	 * @return
	 */
	private String createContext(String groupName) {
		Context context = new Context();
		context.setName(groupName);
		ObjectId contextId = contextRepository.saveAndReturnId(context);
		return contextId.toString();
	}

	/**
	 * Obtains an access token using the login api. Sends the credentials of the user which has been previously created.
	 *
	 * @param user
	 * @return
	 */
	private String obtainAccessToken(User user) {

		JSONObject request = new JSONObject();
		try {
			request.put("username", user.getEmail());
			request.put("password", "test");
		} catch (JSONException e) {
			log.error("Error occured {0}", e.getMessage());
		}

		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
		ResponseEntity<String> loginResponse = restTemplate.exchange(loadedConfigItems.getLoginAPI(), HttpMethod.POST, entity, String.class);

		if (loginResponse.getStatusCode() == HttpStatus.OK) {
			List<String> all_headers = loginResponse.getHeaders().get("Authorization");
			return all_headers.get(0).toString();
		}

		return null;
	}

	/**
	 * Creates http headers which are used in each API call. Access Token and Langugage must be provided in order to retrive the information
	 * from the API.
	 *
	 * @return
	 */
	public HttpHeaders createHttpHeaders(UserRole userRole) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept-Language", "en");
		if (userRole.equals(UserRole.ADMIN)) {
			headers.set("Authorization", getAccessTokenAdmin());
		} else if (userRole.equals(UserRole.SUPERUSER)) {
			headers.set("Authorization", getAccessTokenSuperUser());
		} else {
			headers.set("Authorization", getAccessTokenProbe());
		}
		return headers;
	}

	/**
	 * Creates http headers without access token. This function is used mostly for unauthorized tests.
	 *
	 * @return
	 */
	public HttpHeaders createHttpHeadersWithoutAccessToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept-Language", "en");
		return headers;
	}

	public String getAccessTokenAdmin() {
		return accessTokenAdmin;
	}

	public void setAccessTokenAdmin(String accessToken) {
		accessTokenAdmin = accessToken;
	}

	public String getAccessTokenProbe() {
		return accessTokenProbe;
	}

	public void setAccessTokenProbe(String accessTokenProbe) {
		this.accessTokenProbe = accessTokenProbe;
	}

	public String getAccessTokenSuperUser() {
		return accessTokenSuperUser;
	}

	public void setAccessTokenSuperUser(String accessTokenSuperUser) {
		this.accessTokenSuperUser = accessTokenSuperUser;
	}

	public User getAdminUser() {
		return adminUser;
	}

	public User getProbeUser() {
		return probeUser;
	}

	public User getSuperUser() {
		return superUser;
	}
}
