package com.simple2secure.portal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.SystemUnderTestRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.NotificationUtils;

import simple2secure.validator.annotation.ValidInput;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputGroup;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputPage;
import simple2secure.validator.model.ValidInputSize;

@RestController
@RequestMapping(StaticConfigItems.SUT_API)
public class SystemUnderTestController {

	private static Logger log = LoggerFactory.getLogger(SystemUnderTestController.class);

	@Autowired
	SystemUnderTestRepository sutRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	NotificationUtils notificationUtils;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@ValidRequestMapping(method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SystemUnderTest> addNewSUT(@RequestBody SystemUnderTest sut, @ValidInput ValidInputLocale locale) {
		if (sut != null) {
			sutRepository.save(sut);
			log.debug("System Under Test: {} has been saved", sut.getName());

			return new ResponseEntity<>(sut, HttpStatus.OK);
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_sut", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getAllSUT(@PathVariable ValidInputGroup groupId, @PathVariable ValidInputPage page,
			@PathVariable ValidInputSize size, @ValidInput ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(locale.getValue()) && !Strings.isNullOrEmpty(groupId.getValue())) {
			List<SystemUnderTest> allSUTFromDb = sutRepository.getByGroupId(groupId.getValue(), page.getValue(), size.getValue());
			Map<String, Object> sutMap = new HashMap<>();
			if (allSUTFromDb != null) {
				sutMap.put("sutList", allSUTFromDb);
				sutMap.put("totalSize", sutRepository.getCountOfSUTWithGroupId(groupId.getValue()));
				return new ResponseEntity<>(sutMap, HttpStatus.OK);
			}
			log.debug("Successfully loaded Systems Under Test!");
		}
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_loading_sut", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}
}