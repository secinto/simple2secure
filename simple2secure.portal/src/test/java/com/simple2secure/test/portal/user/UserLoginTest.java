package com.simple2secure.test.portal.user;

import java.util.List;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.simple2secure.api.model.User;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.repository.UserRepository;

@RunWith(SpringRunner.class)
@ComponentScan(basePackages = ("com.simple2secure.test.portal"))
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class,
		EmbeddedMongoAutoConfiguration.class, MongoAutoConfiguration.class })

// @DataMongoTest
public class UserLoginTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private User user;

	String token;

	@Before
	public void init() throws Exception {
		user = new User();

		user.setFirstName("test");
		user.setLastName("test");
		user.setUsername("test");
		user.setEmail("testiing@test.com");
		user.setPassword(this.passwordEncoder.encode("test"));
		user.setUsername("test");
		user.setActivated(true);
		user.setActivationToken("12345");

		this.userRepository.save(user);
	}

	@After
	public void tearDown() {
		this.userRepository.deleteAll();
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

		ResponseEntity<String> loginResponse = restTemplate.exchange("http://localhost:" + port + "/api/login",
				HttpMethod.POST, entity, String.class);

		if (loginResponse.getStatusCode() == HttpStatus.OK) {
			List<String> all_headers = loginResponse.getHeaders().get("Authorization");
			token = all_headers.get(0);
		} else if (loginResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			token = null;
		}

		Assert.assertNotNull(token);
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

		ResponseEntity<String> loginResponse = restTemplate.exchange("http://localhost:" + port + "/api/login",
				HttpMethod.POST, entity, String.class);

		if (loginResponse.getStatusCode() == HttpStatus.OK) {
			List<String> all_headers = loginResponse.getHeaders().get("Authorization");
			token = all_headers.get(0);
		} else if (loginResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			token = null;
		}
		Assert.assertNull(token);
	}

	@Test
	public void testRegisterUserSuccessfull() {
		JSONObject request = new JSONObject();
		request.put("username", "testiing@test.com");
		request.put("password", "testss");
	}

	@After
	public void deleteUser() {
		User retrievedUser = this.userRepository.findByEmailOnlyActivated(user.getEmail());
		this.userRepository.delete(retrievedUser);
	}

}
