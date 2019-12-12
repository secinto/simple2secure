package com.simple2secure.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.DeviceStatus;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.providers.BaseUtilsProvider;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputContext;
import simple2secure.validator.model.ValidInputDeviceType;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputPage;
import simple2secure.validator.model.ValidInputSize;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.SUT_API)
public class SystemUnderTestController extends BaseUtilsProvider {

	private static Logger log = LoggerFactory.getLogger(SystemUnderTestController.class);

	@ValidRequestMapping(value = "/add",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SystemUnderTest> addNewSUT(@RequestBody SystemUnderTest sut, @ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale) {
		if (sut != null && contextId.getValue() != null) {
			sut.setContextId(contextId.getValue());
			sutRepository.save(sut);
			log.debug("System Under Test: {} has been saved", sut.getName());

			return new ResponseEntity<>(sut, HttpStatus.OK);
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_sut", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getSUTList(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputDeviceType deviceType, @PathVariable ValidInputPage page, @PathVariable ValidInputSize size,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(locale.getValue()) && !Strings.isNullOrEmpty(contextId.getValue()) && deviceType != null) {
			if (contextId != null) {
				List<SystemUnderTest> suts = sutRepository.getByContextIdAndType(contextId.getValue(), page.getValue(), size.getValue(),
						deviceType.getValue());
				if (suts != null && suts.size() > 0) {
					for (SystemUnderTest sut : suts) {
						DeviceStatus status = sut.getDeviceStatus();
						DeviceStatus deviceStatus = sutUtils.getDeviceStatus(sut);
						if (status != deviceStatus) {
							sut.setDeviceStatus(deviceStatus);
						}
					}

					Map<String, Object> sutMap = new HashMap<>();

					long count = sutRepository.getTotalAmountOfSystemUnderTest(contextId.getValue(), deviceType.getValue());
					sutMap.put("sutList", suts);
					sutMap.put("totalSize", count);

					return new ResponseEntity<>(sutMap, HttpStatus.OK);
				}else {
					Map<String, Object> emptySUTMap = new HashMap<>();
					long count = 0;
					emptySUTMap.put("sutList", new ArrayList<SystemUnderTest>());
					emptySUTMap.put("totalSize", count);
					return new ResponseEntity<>(emptySUTMap, HttpStatus.OK);
				}
			}

		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_sut", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}
}