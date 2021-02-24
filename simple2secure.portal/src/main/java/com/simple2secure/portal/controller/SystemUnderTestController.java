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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputSut;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.SUT_API)
@Slf4j
public class SystemUnderTestController extends BaseUtilsProvider {

	/**
	 * This method stores the newly created @SystemUnderTest in the database.
	 *
	 * @param sut
	 *          The @SystemUnderTest which is going to be stored in the database.
	 * @param contextId
	 *          The context for which this has been performed.
	 * @param locale
	 *          The current locale used by the user.
	 * @return The in the database stored @SystemUnderTest.
	 */
	@ValidRequestMapping(
			value = "/addSut",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SystemUnderTest> addNewSut(@RequestBody SystemUnderTest sut, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (sut != null && contextId.getValue() != null) {
			sut.setContextId(contextId.getValue());
			sutRepository.save(sut);
			log.debug("System Under Test: {} has been saved", sut.getName());

			return new ResponseEntity<>(sut, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_saving_sut", locale.getValue()));
	}

	/**
	 * This methods updates the provided @SystemUnderTest in the database.
	 *
	 * @param sut
	 *          The @SystemUnderTest which is going to be updated in the database.
	 * @param contextId
	 *          The context for which this has been performed.
	 * @param locale
	 *          The current locale used by the user.
	 * @return The updated @SystemUnderTest from the database.
	 */
	@ValidRequestMapping(
			value = "/updateSut",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SystemUnderTest> updateSut(@RequestBody SystemUnderTest sut, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (sut != null && contextId.getValue() != null) {
			try {
				sutRepository.update(sut);
				log.debug("System Under Test: {} has been updated", sut.getName());
			} catch (ItemNotFoundRepositoryException e) {
				log.error("Error occured while updating the System Under Test ", e);
			}
			return new ResponseEntity<>(sut, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_updating_sut", locale.getValue()));
	}

	/**
	 * This method imports the provided list of the suts
	 * 
	 * @param sutList
	 * @param contextId
	 * @param locale
	 * @return
	 */
	@ValidRequestMapping(
			value = "/importSut",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<SystemUnderTest>> addNewSut(@RequestBody List<SystemUnderTest> sutList,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale) {
		if (sutList != null && contextId.getValue() != null) {
			sutList = sutUtils.importSuts(sutList, contextId.getValue());
			return new ResponseEntity<>(sutList, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_saving_sut", locale.getValue()));
	}

	/**
	 * This method retrieves all @SystemUnderTest for the provided context.
	 *
	 * @param contextId
	 *          The context for which this has been performed.
	 * @param page
	 * @param size
	 * @param locale
	 *          The current locale used by the user.
	 * @return A list of @SystemUnderTest belonging to the provided context.
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getAllSUTsForContext(@ServerProvidedValue ValidInputContext contextId, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (contextId.getValue() != null) {

			Map<String, Object> sutMap = sutRepository.getAllByContextIdPaged(contextId.getValue(), page, size, filter);
			
			return new ResponseEntity<>(sutMap, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_loading_sut", locale.getValue()));
	}

	/**
	 * This method deletes the @SystemUnderTest with the provided id.
	 *
	 * @param sutId
	 *          The provided id of the @SystemUnderTest
	 * @param locale
	 *          The current locale used by the user.
	 * @return A response with status code 200 and a null.
	 */
	@ValidRequestMapping(
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SystemUnderTest> deleteSUT(@PathVariable ValidInputSut sutId, @ServerProvidedValue ValidInputLocale locale) {
		if (sutId != null) {
			sutRepository.delete(sutRepository.find(sutId.getValue()));
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_deleting_sut", locale.getValue()));
	}
}