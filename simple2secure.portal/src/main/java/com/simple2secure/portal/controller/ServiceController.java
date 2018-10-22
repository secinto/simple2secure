/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.simple2secure.api.config.ConfigItems;
import com.simple2secure.api.model.Service;

@RestController
public class ServiceController {

	RestTemplate restTemplate = new RestTemplate();

	/**
	 * This API returns JSON, with all services listed in it.
	 *
	 * @return
	 */
	@RequestMapping(value = "/api", method = RequestMethod.GET)
	public ResponseEntity<List<Service>> getAvailableServices(@RequestHeader("Accept-Language") String locale) {

		ResponseEntity<Service[]> response = this.restTemplate.getForEntity(ConfigItems.services_url, Service[].class);
		List<Service> servicesList = Arrays.asList(response.getBody());
		return new ResponseEntity<List<Service>>(servicesList, HttpStatus.OK);
	}
}
