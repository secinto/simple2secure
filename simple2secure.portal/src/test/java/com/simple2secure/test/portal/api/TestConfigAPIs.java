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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.gson.Gson;
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

	private Gson gson = new Gson();

	@PostConstruct
	public void init() {
		/*
		 * Update the port to the one randomly selected by the framework. Otherwise the URLs would be incorrect.
		 */
		loadedConfigItems.setBasePort(String.valueOf(randomServerPort));
	}

	@Test
	public void testGetSettingsConfig() throws IOException {
		File file = new File(getClass().getResource("/server/settings.json").getFile());
		String content = new String(Files.readAllBytes(file.toPath()));
		Settings settings = gson.fromJson(content, Settings.class);

		assertNotNull(settings);

		log.debug("Test response {}", content);
	}
}
