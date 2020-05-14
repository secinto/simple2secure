package com.simple2secure.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputPage;
import com.simple2secure.portal.validation.model.ValidInputSize;
import com.simple2secure.portal.validation.model.ValidInputSut;
import com.simple2secure.portal.validation.model.ValidInputSystemType;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping(StaticConfigItems.SUT_API)
@Slf4j
public class SystemUnderTestController extends BaseUtilsProvider {

	@ValidRequestMapping(value = "/addSut", method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SystemUnderTest> addNewLDCSUT(@RequestBody SystemUnderTest sut, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (sut != null && contextId.getValue() != null) {
			sut.setContextId(contextId.getValue());
			sutRepository.save(sut);
			log.debug("System Under Test: {} has been saved", sut.getName());

			return new ResponseEntity<>(sut, HttpStatus.OK);
		}

		return ((ResponseEntity<SystemUnderTest>) buildResponseEntity("problem_occured_while_saving_sut", locale));
	}

	@ValidRequestMapping(value = "/updateSut", method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SystemUnderTest> updateLDC(@RequestBody SystemUnderTest sut, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (sut != null && contextId.getValue() != null) {
			SystemUnderTest sutToUpdate = sutUtils.updateSut(sut);
			try {
				sutRepository.update(sutToUpdate);
				log.debug("System Under Test: {} has been updated", sutToUpdate.getName());
			} catch (ItemNotFoundRepositoryException e) {
				log.error("Error occured while updating the System Under Test ", e);
			}

			return new ResponseEntity<>(sutToUpdate, HttpStatus.OK);
		}

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
				count = sutRepository.getTotalAmountOfSystemUnderTestWithType(contextId.getValue(), systemType.getValue());
				sutMap.put("sutList", suts);
				sutMap.put("totalSize", count);
			}

			return new ResponseEntity<>(sutMap, HttpStatus.OK);
		}
		return ((ResponseEntity<Map<String, Object>>) buildResponseEntity("problem_occured_while_saving_sut", locale));
	}

	@ValidRequestMapping(method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SystemUnderTest> deleteSUT(@PathVariable ValidInputSut sutId, @ServerProvidedValue ValidInputLocale locale) {
		if (sutId != null) {
			sutRepository.delete(sutRepository.find(sutId.getValue()));
			return new ResponseEntity<>(null, HttpStatus.OK);
		}

		return ((ResponseEntity<SystemUnderTest>) buildResponseEntity("problem_occured_while_saving_sut", locale));
	}
}