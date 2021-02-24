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

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.repository.SettingsRepository;

@ActiveProfiles("test")
@ExtendWith({ SpringExtension.class })
@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = { Simple2SecurePortal.class })
public class TestAPIBase {

	@Autowired
	protected LoadedConfigItems loadedConfigItems;

	@Autowired
	private SettingsRepository settingsRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@LocalServerPort
	protected int randomServerPort;

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
		createSettings();
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
	 * Creates http headers which are used in each API call. Access Token and Langugage must be provided in order to retrive the information
	 * from the API.
	 *
	 * @return
	 */
	public HttpHeaders createHttpHeaders(UserRole userRole) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept-Language", "en");
		headers.set("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
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
}
