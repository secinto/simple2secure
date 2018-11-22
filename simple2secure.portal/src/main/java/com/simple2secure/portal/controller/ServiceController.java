package com.simple2secure.portal.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.simple2secure.api.dto.ServiceLibraryDTO;
import com.simple2secure.api.model.Service;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ServiceLibraryRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
public class ServiceController {

	@Autowired
	ServiceLibraryRepository serviceLibraryRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@RequestMapping(value = "/api/service/", method = RequestMethod.GET)
	public ResponseEntity<Service> getServiceVersion(@RequestHeader("Accept-Language") String locale) {
		return new ResponseEntity<Service>(new Service("ProbeService", "0.1.0"), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/service/{versionId}", method = RequestMethod.GET)
	public ResponseEntity<ServiceLibraryDTO> getServiceVersion(@PathVariable("version") String version,
			@RequestHeader("Accept-Language") String locale) {
		ServiceLibraryDTO library = (ServiceLibraryDTO) serviceLibraryRepository.findByVersion(version);

		try {
			library.setLibraryData(Files.readAllBytes(new File(library.getFilename()).toPath()));
		} catch (IOException ioe) {
			library = null;
		}

		if (library != null) {
			return new ResponseEntity<ServiceLibraryDTO>(library, HttpStatus.OK);
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("service_not_found", locale)), HttpStatus.NOT_FOUND);

		}
	}

}
