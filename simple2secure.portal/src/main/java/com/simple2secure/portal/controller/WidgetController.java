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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.WidgetDTO;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.Widget;
import com.simple2secure.api.model.WidgetProperties;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.WidgetPropertiesRepository;
import com.simple2secure.portal.repository.WidgetRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.DeviceUtils;
import com.simple2secure.portal.utils.WidgetUtils;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.annotation.WidgetFunction;
import simple2secure.validator.model.ValidInputContext;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputUser;
import simple2secure.validator.model.ValidInputWidget;
import simple2secure.validator.model.ValidInputWidgetProp;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.WIDGET_API)
public class WidgetController {

	@Autowired
	WidgetRepository widgetRepository;

	@Autowired
	WidgetPropertiesRepository widgetPropertiesRepository;

	@Autowired
	WidgetUtils widgetUtils;

	@Autowired
	DeviceUtils deviceUtils;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	List<String> widgetFunctions = new ArrayList<>();

	static final Logger log = LoggerFactory.getLogger(WidgetController.class);

	@PostConstruct
	public void initialize() {

		final List<Method> allMethods = new ArrayList<>(Arrays.asList(WidgetController.class.getDeclaredMethods()));

		for (final Method method : allMethods) {
			if (method.isAnnotationPresent(WidgetFunction.class)) {
			}
		}
	}

	@ValidRequestMapping()
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<Widget>> getAllWidgets(@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(locale.getValue())) {
			List<Widget> widgets = widgetRepository.findAll();
			return new ResponseEntity<>(widgets, HttpStatus.OK);
		}
		log.error("Problem occured while retrieving widgets");
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_widgets", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/delete",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<Widget> deleteWidget(@PathVariable ValidInputWidget widgetId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(widgetId.getValue())) {
			Widget widget = widgetRepository.find(widgetId.getValue());
			if (widget != null) {
				widgetRepository.delete(widget);
				return new ResponseEntity<>(widget, HttpStatus.OK);
			}
		}
		log.error("Problem occured while deleting widget with id {}", widgetId.getValue());
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_widget", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	@ValidRequestMapping(
			value = "/add",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<Widget> saveWidget(@RequestBody Widget widget, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (widget != null) {
			if (Strings.isNullOrEmpty(widget.getId())) {
				widgetRepository.save(widget);
			} else {
				widgetRepository.update(widget);
			}
			return new ResponseEntity<>(widget, HttpStatus.OK);
		}
		log.error("Problem occured while saving widget");
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_settings", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/updatePosition",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<WidgetProperties> updateWidgetPosition(@RequestBody WidgetDTO widgetDTO, @ServerProvidedValue ValidInputUser userId,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (widgetDTO != null) {
			if (Strings.isNullOrEmpty(widgetDTO.getWidgetProperties().getId())) {
				widgetDTO.getWidgetProperties().setContextId(contextId.getValue());
				widgetDTO.getWidgetProperties().setUserId(userId.getValue());
				ObjectId widgetPropertiesId = widgetPropertiesRepository.saveAndReturnId(widgetDTO.getWidgetProperties());
				widgetDTO.getWidgetProperties().setId(widgetPropertiesId.toString());
			} else {
				widgetPropertiesRepository.update(widgetDTO.getWidgetProperties());
			}
			return new ResponseEntity<>(widgetDTO.getWidgetProperties(), HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_settings", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/get")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<WidgetDTO>> getWidgetDTOByUserId(@ServerProvidedValue ValidInputUser userId,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(userId.getValue()) && !Strings.isNullOrEmpty(contextId.getValue())) {
			List<WidgetDTO> widgets = widgetUtils.getWidgetsByUserAndContextId(userId.getValue(), contextId.getValue());
			return new ResponseEntity<>(widgets, HttpStatus.OK);
		}
		log.error("Problem occured while retrieving widgets");
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_widgets", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/delete/prop",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<WidgetProperties> deleteWidgetProperty(@PathVariable ValidInputWidgetProp widgetPropId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(widgetPropId.getValue())) {
			WidgetProperties widgetProp = widgetPropertiesRepository.find(widgetPropId.getValue());
			if (widgetProp != null) {
				widgetPropertiesRepository.delete(widgetProp);
				return new ResponseEntity<>(widgetProp, HttpStatus.OK);
			}
		}
		log.error("Problem occured while deleting widget with id {}", widgetPropId.getValue());
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_widget", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@WidgetFunction
	@ValidRequestMapping(
			value = "/devActive")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Integer> countActiveDevices(@ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				List<Device> devices = deviceUtils.getAllDevicesFromCurrentContext(context, true);
				return new ResponseEntity<>(devices.size(), HttpStatus.OK);
			}
		}
		log.error("Problem occured while retrieving widgets");
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_widgets", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

}
