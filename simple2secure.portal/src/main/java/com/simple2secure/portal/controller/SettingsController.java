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

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simple2secure.api.dto.SettingsDTO;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.TestMacro;
import com.simple2secure.api.model.Widget;
import com.simple2secure.api.model.WidgetConfig;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputLicensePlan;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputTestMacro;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.SETTINGS_API)
@Slf4j
public class SettingsController extends BaseUtilsProvider {

	@ValidRequestMapping
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<SettingsDTO> getSettings(@ServerProvidedValue ValidInputLocale locale) {
		List<Settings> settings = settingsRepository.findAll();
		List<LicensePlan> licensePlans = licensePlanRepository.findAll();
		List<TestMacro> testMacros = testMacroRepository.findAll();
		List<Widget> widgets = widgetRepository.findAll();
		WidgetConfig widgetConfig = widgetUtils.getWidgetConfig();
		if (settings != null) {
			if (settings.size() == 1) {
				return new ResponseEntity<>(new SettingsDTO(settings.get(0), licensePlans, testMacros, widgets, widgetConfig), HttpStatus.OK);
			}
		}
		log.error("Problem occured while retrieving settings");
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_retrieving_settings", locale.getValue()));

	}

	@ValidRequestMapping(
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<Settings> updateSettings(@RequestBody Settings settings, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (settings != null) {
			settingsRepository.update(settings);
			return new ResponseEntity<>(settings, HttpStatus.OK);
		}
		log.error("Problem occured while updating settings");
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_updating_settings", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/licensePlan",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<LicensePlan> saveLicensePlan(@RequestBody LicensePlan licensePlan, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (licensePlan != null) {
			if (licensePlan.getId() != null) {
				licensePlanRepository.save(licensePlan);
			} else {
				licensePlanRepository.update(licensePlan);
			}
			return new ResponseEntity<>(licensePlan, HttpStatus.OK);
		}
		log.error("Problem occured while saving license plan");
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_updating_settings", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/licensePlan",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<LicensePlan> deleteLicensePlan(@PathVariable ValidInputLicensePlan licensePlanId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (licensePlanId.getValue() != null) {
			LicensePlan licensePlan = licensePlanRepository.find(licensePlanId.getValue());
			if (licensePlan != null) {
				licensePlanRepository.delete(licensePlan);
				return new ResponseEntity<>(licensePlan, HttpStatus.OK);
			}
		}
		log.error("Problem occured while deleting license plan with id {}", licensePlanId);
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_deleting_license_plan", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/testmacro",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<TestMacro> deleteTestMacro(@PathVariable ValidInputTestMacro testMacroId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (testMacroId.getValue() != null) {
			TestMacro testMacro = testMacroRepository.find(testMacroId.getValue());
			if (testMacro != null) {
				testMacroRepository.delete(testMacro);
				return new ResponseEntity<>(testMacro, HttpStatus.OK);
			}
		}
		log.error("Problem occured while deleting test macro with id {}", testMacroId.getValue());
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_deleting_test_macro", locale.getValue()));

	}

	@ValidRequestMapping(
			value = "/testmacro",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<TestMacro> saveTestMacro(@RequestBody TestMacro testMacro, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (testMacro != null) {
			if (testMacro.getId() != null) {
				testMacroRepository.save(testMacro);
			} else {
				testMacroRepository.update(testMacro);
			}
			return new ResponseEntity<>(testMacro, HttpStatus.OK);
		}
		log.error("Problem occured while saving test macro");
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_updating_settings", locale.getValue()));
	}
}
