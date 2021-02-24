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

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simple2secure.api.model.TestInputData;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputTestDataInput;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.INPUTDATA_API)
@Slf4j
public class InputDataController extends BaseUtilsProvider {

	@ValidRequestMapping(
			method = ValidRequestMethodType.POST)
	public ResponseEntity<TestInputData> addUpdateInputData(@RequestBody TestInputData testInputData,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (testInputData != null) {
			if (testInputData.getId() == null) {
				// save
				ObjectId testInputDataId = testInputDataRepository.saveAndReturnId(testInputData);
				testInputData.setId(testInputDataId);
			} else {
				// update
				testInputDataRepository.update(testInputData);
			}

			return new ResponseEntity<>(testInputData, HttpStatus.OK);
		}

		throw new ApiRequestException(messageByLocaleService.getMessage("error_saving_input_data", locale.getValue()));
	}

	/**
	 * This function deletes input data by id
	 */
	@ValidRequestMapping(
			value = "/delete",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestInputData> deleteInputData(@PathVariable ValidInputTestDataInput inputDataId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (inputDataId.getValue() != null) {
			TestInputData inputData = testInputDataRepository.find(inputDataId.getValue());
			if (inputData != null) {
				testInputDataRepository.delete(inputData);

				return new ResponseEntity<>(inputData, HttpStatus.OK);
			}
		}
		log.error("Error occured while deleting input data with id {}", inputDataId.getValue());
		throw new ApiRequestException(messageByLocaleService.getMessage("error_deleting_input_data", locale.getValue()));
	}
}
