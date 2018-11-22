/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.simple2secure.api.model.Probe;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.DeviceRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
public class DeviceController {

	@Autowired
	DeviceRepository deviceRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/api/device/{deviceId}/{userId}", method = RequestMethod.GET)
	public ResponseEntity<Probe> getDevice(@PathVariable("deviceId") String deviceId, @PathVariable("userId") String userId,
			@RequestHeader("Accept-Language") String locale) {
		Probe device = deviceRepository.findByProbeAndUserId(deviceId, userId);

		if (device != null) {
			return new ResponseEntity<Probe>(device, HttpStatus.OK);
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("device_not_found", locale)), HttpStatus.NOT_FOUND);
		}
	}
}
