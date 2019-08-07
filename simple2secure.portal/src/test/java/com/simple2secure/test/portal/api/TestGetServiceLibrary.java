package com.simple2secure.test.portal.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple2secure.api.model.Service;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.rest.RESTUtils;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.test.portal.utils.TLSConfig;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestGetServiceLibrary {

	private static Logger log = LoggerFactory.getLogger(TestGetServiceLibrary.class);

	@Autowired
	protected LoadedConfigItems loadedConfigItems;

	@LocalServerPort
	protected int randomServerPort;

	@PostConstruct
	public void init() {
		/*
		 * Update the port to the one randomly selected by the framework. Otherwise the URLs would be incorrect.
		 */
		loadedConfigItems.setBasePort(String.valueOf(randomServerPort));
	}

	@BeforeAll
	public static void setupSSL() {
		TLSConfig.sslContextConfiguration();
	}

	@Test
	public void testGetServiceCurrent() throws Exception {
		String response = RESTUtils.sendGet(loadedConfigItems.getServiceAPI() + "/");
		assertNotNull(response);
		log.debug("Response received {}", response.toString());
		ObjectMapper objectMapper = new ObjectMapper();
		Service version = objectMapper.readValue(response.toString(), Service.class);
		assertNotNull(version);
		assertEquals(version.getVersion(), "0.1.0");
	}
}
