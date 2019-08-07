package com.simple2secure.test.portal.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.simple2secure.api.model.Settings;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.Simple2SecurePortal;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestConfigAPIs {

	private static Logger log = LoggerFactory.getLogger(TestConfigAPIs.class);

	@Autowired
	protected LoadedConfigItems loadedConfigItems;

	@LocalServerPort
	protected int randomServerPort;

	@Autowired
	private TestRestTemplate restTemplate;

	@PostConstruct
	public void init() {
		/*
		 * Update the port to the one randomly selected by the framework. Otherwise the URLs would be incorrect.
		 */
		loadedConfigItems.setBasePort(String.valueOf(randomServerPort));
	}

	@Test
	public void testGetSettingsConfig() {
		ResponseEntity<Settings> response = restTemplate.getForEntity(loadedConfigItems.getSettingsURL(), Settings.class);
		assertNotNull(response);

		assertEquals(200, response.getStatusCodeValue());

		log.debug("Test response {}", response.toString());
	}

	@Test
	public void testGetConfigConfig() {
		ResponseEntity<Settings> response = restTemplate.getForEntity(loadedConfigItems.getConfigURL(), Settings.class);
		assertNotNull(response);

		assertEquals(200, response.getStatusCodeValue());

		log.debug("Test response {}", response.toString());
	}

}
