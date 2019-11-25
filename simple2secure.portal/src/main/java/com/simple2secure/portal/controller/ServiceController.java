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
package com.simple2secure.portal.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.simple2secure.api.dto.ServiceLibraryDTO;
import com.simple2secure.api.model.Service;
import com.simple2secure.api.model.ValidInputLocale;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ServiceLibraryRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.validator.ValidInput;

@RestController
@RequestMapping(StaticConfigItems.SERVICE_API)
public class ServiceController {

	@Autowired
	ServiceLibraryRepository serviceLibraryRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Service> getServiceVersion(@ValidInput ValidInputLocale locale) {
		return new ResponseEntity<>(new Service("simple2secure", loadedConfigItems.getVersion()), HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{version}", method = RequestMethod.GET)
	public ResponseEntity<ServiceLibraryDTO> getServiceVersion(@PathVariable String version, @ValidInput ValidInputLocale locale) {
		ServiceLibraryDTO library = (ServiceLibraryDTO) serviceLibraryRepository.findByVersion(version);

		try {
			library.setLibraryData(Files.readAllBytes(new File(library.getFilename()).toPath()));
		} catch (IOException ioe) {
			library = null;
		}

		if (library != null) {
			return new ResponseEntity<>(library, HttpStatus.OK);
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("service_not_found", locale.getValue())),
					HttpStatus.NOT_FOUND);

		}
	}

}
