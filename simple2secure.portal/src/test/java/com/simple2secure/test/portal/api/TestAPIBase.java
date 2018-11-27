package com.simple2secure.test.portal.api;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

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

import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.Simple2SecurePortal;
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
	UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	MongoTemplate mongoTemplate;

	@LocalServerPort
	protected int randomServerPort;

	private User adminUser;

	private User probeUser;

	private String accessTokenAdmin;

	private String accessTokenProbe;

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
		createSettings();
		setAccessTokenAdmin(obtainAccessToken(adminUser));
		setAccessTokenProbe(obtainAccessToken(probeUser));
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

		user.setFirstName("test");
		user.setLastName("test");
		user.setEmail("testiing@test.com");
		user.setPassword(passwordEncoder.encode("test"));
		user.setUsername("testiing@test.com");
		user.setActivated(true);
		user.setActivationToken("12345");
		user.setUserRole(userRole);

		if (userRole.equals(UserRole.PROBE)) {
			user.setEmail("probe@test.com");
			user.setUsername("probe");
			user.setActivationToken("54321");
		}

		userRepository.save(user);

		return user;
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
		HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
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

	public User getAdminUser() {
		return adminUser;
	}

	public User getProbeUser() {
		return probeUser;
	}
}
