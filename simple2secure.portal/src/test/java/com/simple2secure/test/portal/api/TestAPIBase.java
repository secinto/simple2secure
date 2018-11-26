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

	@LocalServerPort
	protected int randomServerPort;

	private User user;

	private String accessToken;

	HttpHeaders headers = new HttpHeaders();

	@PostConstruct
	public void init() {

		/*
		 * Update the port to the one randomly selected by the framework. Otherwise the URLs would be incorrect.
		 */
		loadedConfigItems.setBasePort(String.valueOf(randomServerPort));

		user = createUser();
		createSettings();
		setAccessToken(obtainAccessToken(user));
	}

	private void createSettings() {
		Settings settings = new Settings();
		settings.setAccessTokenValidityTime(10);
		settings.setAccessTokenValidityUnit(TimeUnit.MINUTES);
		settingsRepository.save(settings);
	}

	private User createUser() {
		User user = new User();

		user.setFirstName("test");
		user.setLastName("test");
		user.setUsername("test");
		user.setEmail("testiing@test.com");
		user.setPassword(passwordEncoder.encode("test"));
		user.setUsername("testiing@test.com");
		user.setActivated(true);
		user.setActivationToken("12345");
		user.setUserRole(UserRole.ADMIN);
		userRepository.save(user);

		return user;
	}

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

	public HttpHeaders createHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", accessToken);
		return headers;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
