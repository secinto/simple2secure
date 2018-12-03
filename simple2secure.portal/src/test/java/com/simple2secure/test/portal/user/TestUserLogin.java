package com.simple2secure.test.portal.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
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

import com.simple2secure.api.model.User;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.repository.UserRepository;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestUserLogin {

	@Autowired
	UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	protected LoadedConfigItems loadedConfigItems;

	@LocalServerPort
	protected int randomServerPort;

	@Autowired
	private TestRestTemplate restTemplate;

	private static User user;

	String token;

	@BeforeAll
	public void init() {
		user = new User();

		user.setFirstName("test");
		user.setLastName("test");
		user.setUsername("test");
		user.setEmail("testiing@test.com");
		user.setPassword(passwordEncoder.encode("test"));
		user.setUsername("test");
		user.setActivated(true);
		user.setActivationToken("12345");
		userRepository.save(user);

		loadedConfigItems.setBasePort(String.valueOf(randomServerPort));

	}

	/**
	 * This test tries to login user with the correct credentials
	 *
	 * @throws Exception
	 */
	@Test
	public void testUserLoginSuccessfull() throws Exception {

		// List<User> users = this.userRepository.findAll();

		JSONObject request = new JSONObject();
		request.put("username", "testiing@test.com");
		request.put("password", "test");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

		ResponseEntity<String> loginResponse = restTemplate.exchange(loadedConfigItems.getLoginAPI(), HttpMethod.POST, entity, String.class);

		if (loginResponse.getStatusCode() == HttpStatus.OK) {
			List<String> all_headers = loginResponse.getHeaders().get("Authorization");
			token = all_headers.get(0);
		} else if (loginResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			token = null;
		}
		assertNotNull(token);
	}

	/**
	 * This is a negative test which sends a wrong credentials
	 *
	 * @throws Exception
	 */
	@Test
	public void testUserLoginWrongCredentials() throws Exception {

		JSONObject request = new JSONObject();
		request.put("username", "testiing@test.com");
		request.put("password", "testss");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

		ResponseEntity<String> loginResponse = restTemplate.exchange(loadedConfigItems.getLoginAPI(), HttpMethod.POST, entity, String.class);

		if (loginResponse.getStatusCode() == HttpStatus.OK) {
			List<String> all_headers = loginResponse.getHeaders().get("Authorization");
			token = all_headers.get(0);
		} else if (loginResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			token = null;
		}
		assertNull(token);
	}

	@AfterAll
	public void deleteUser() {
		userRepository.deleteAll();
	}

}
