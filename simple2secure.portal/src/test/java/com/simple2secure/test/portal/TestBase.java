package com.simple2secure.test.portal;

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
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.Simple2SecurePortal;

//@ComponentScan(basePackages = "com.simple2secure.portal")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestBase {

	private static Logger log = LoggerFactory.getLogger(TestBase.class);

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@LocalServerPort
	int randomServerPort;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testConfig() {
		loadedConfigItems.setBasePort(String.valueOf(randomServerPort));
		ResponseEntity<Settings> response = restTemplate.getForEntity(loadedConfigItems.getSettingsURL(),
				Settings.class);
		log.debug(response.toString());
	}

}
