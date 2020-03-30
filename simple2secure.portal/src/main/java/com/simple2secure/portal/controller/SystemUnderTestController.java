package com.simple2secure.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.LDCSystemUnderTest;
import com.simple2secure.api.model.Protocol;
import com.simple2secure.api.model.S2SDSL;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.api.model.Test;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputPage;
import com.simple2secure.portal.validation.model.ValidInputSize;
import com.simple2secure.portal.validation.model.ValidInputSystemType;
import com.simple2secure.portal.validation.model.ValidInputTest;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping(StaticConfigItems.SUT_API)
@Slf4j
public class SystemUnderTestController extends BaseUtilsProvider {

	
	@ValidRequestMapping(value = "/add", method = ValidRequestMethodType.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SystemUnderTest> addNewSUT(@ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {
		String brk = "";
		LDCSystemUnderTest sut = new LDCSystemUnderTest();
		sut.setContextId("5e60f9ce51a1130e924fa0ac");
		sut.setDeviceId("1cdf5ac-677b-11ea-9c4d-302432fc893c");
		sut.setName("Test");
		sut.setIpAddress("127.0.0.1");
		sut.setPort("80");
		sut.setProtocol(Protocol.HTTP);
		sutRepository.save(sut);
//		if (sut != null && contextId.getValue() != null) {
//			sut.setContextId(contextId.getValue());
//			sutRepository.save(sut);
//			log.debug("System Under Test: {} has been saved", sut.getName());
//
//			return new ResponseEntity<>(sut, HttpStatus.OK);
//		}

		return ((ResponseEntity<SystemUnderTest>) buildResponseEntity("problem_occured_while_saving_sut", locale));
	}

	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getAllSUTsForContext(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputPage page, @PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(locale.getValue()) && !Strings.isNullOrEmpty(contextId.getValue())) {

			Map<String, Object> sutMap = new HashMap<>();
			long count = 0;
			sutMap.put("sutList", new ArrayList<SystemUnderTest>());
			sutMap.put("totalSize", count);

			List<SystemUnderTest> suts = sutRepository.getAllByContextIdPaged(contextId.getValue(), page.getValue(), size.getValue());
			if (suts != null && suts.size() > 0) {
				for (SystemUnderTest sut : suts) {
					/*
					 * Updates the device status for all systems under test.
					 *
					 * TODO: Check if there is a better place or additional places. Because this could also be done in a scheduler or any other
					 * recurring task.
					 */
//					DeviceStatus status = sut.getDeviceStatus();
//					DeviceStatus deviceStatus = sutUtils.getDeviceStatus(sut);
//					if (status != deviceStatus) {
//						sut.setDeviceStatus(deviceStatus);
//					}
				}

				count = sutRepository.getTotalAmountOfSystemUnderTest(contextId.getValue());
				sutMap.put("sutList", suts);
				sutMap.put("totalSize", count);
			}

			return new ResponseEntity<>(sutMap, HttpStatus.OK);
		}
		return ((ResponseEntity<Map<String, Object>>) buildResponseEntity("problem_occured_while_saving_sut", locale));
	}

	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getAllSUTsForContextAndType(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputSystemType systemType, @PathVariable ValidInputPage page, @PathVariable ValidInputSize size,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(locale.getValue()) && !Strings.isNullOrEmpty(contextId.getValue()) && systemType != null
				&& !Strings.isNullOrEmpty(systemType.getValue())) {

			Map<String, Object> sutMap = new HashMap<>();
			long count = 0;
			sutMap.put("sutList", new ArrayList<SystemUnderTest>());
			sutMap.put("totalSize", count);

			List<SystemUnderTest> suts = sutRepository.getAllByContextIdAndSystemTypePaged(contextId.getValue(), page.getValue(), size.getValue(),
					systemType.getValue());
			if (suts != null && suts.size() > 0) {
				for (SystemUnderTest sut : suts) {
					/*
					 * Updates the device status for all systems under test.
					 *
					 * TODO: Check if there is a better place or additional places. Because this could also be done in a scheduler or any other
					 * recurring task.
					 */
//					DeviceStatus status = sut.getDeviceStatus();
//					DeviceStatus deviceStatus = sutUtils.getDeviceStatus(sut);
//					if (status != deviceStatus) {
//						sut.setDeviceStatus(deviceStatus);
//					}
				}

				count = sutRepository.getTotalAmountOfSystemUnderTestWithType(contextId.getValue(), systemType.getValue());
				sutMap.put("sutList", suts);
				sutMap.put("totalSize", count);
			}

			return new ResponseEntity<>(sutMap, HttpStatus.OK);
		}
		return ((ResponseEntity<Map<String, Object>>) buildResponseEntity("problem_occured_while_saving_sut", locale));
	}
	
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getAllSUTsForTest(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputTest testId, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		Reflections reflections = new Reflections("com.simple2secure.api.model");
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(S2SDSL.class);
		Test test = testRepository.find(testId.getValue());
		String testValue = "ldc.sut";
		for(Class clazz : annotated) {
			S2SDSL annotation =  (S2SDSL) clazz.getAnnotation(S2SDSL.class);
			annotation.value();
			String brk = "";
		}
		
		return ((ResponseEntity<Map<String, Object>>) buildResponseEntity("problem_occured_while_saving_sut", locale));
	}
}