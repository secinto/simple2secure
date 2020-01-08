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

import org.bson.types.ObjectId;
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
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputUser;
import com.simple2secure.portal.validation.model.ValidInputWidget;
import com.simple2secure.portal.validation.model.ValidInputWidgetProp;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.annotation.WidgetFunction;
import simple2secure.validator.model.ValidRequestMethodType;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping(StaticConfigItems.WIDGET_API)
@Slf4j
public class WidgetController extends BaseUtilsProvider {

	@ValidRequestMapping()
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<Widget>> getAllWidgets(@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(locale.getValue())) {
			List<Widget> widgets = widgetRepository.findAll();
			return new ResponseEntity<>(widgets, HttpStatus.OK);
		}
		log.error("Problem occured while retrieving widgets");
		return ((ResponseEntity<List<Widget>>) buildResponseEntity("problem_occured_while_retrieving_widgets", locale));
	}

	@ValidRequestMapping(value = "/delete", method = ValidRequestMethodType.DELETE)
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
		return ((ResponseEntity<Widget>) buildResponseEntity("problem_occured_while_deleting_widget", locale));

	}

	@ValidRequestMapping(value = "/add", method = ValidRequestMethodType.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
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
		return ((ResponseEntity<Widget>) buildResponseEntity("problem_occured_while_updating_settings", locale));
	}

	@ValidRequestMapping(value = "/updatePosition", method = ValidRequestMethodType.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
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
		return ((ResponseEntity<WidgetProperties>) buildResponseEntity("problem_occured_while_updating_settings", locale));
	}

	@ValidRequestMapping(value = "/get")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<WidgetDTO>> getWidgetDTOByUserId(@ServerProvidedValue ValidInputUser userId,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(userId.getValue()) && !Strings.isNullOrEmpty(contextId.getValue())) {
			List<WidgetDTO> widgets = widgetUtils.getWidgetsByUserAndContextId(userId.getValue(), contextId.getValue());
			return new ResponseEntity<>(widgets, HttpStatus.OK);
		}
		log.error("Problem occured while retrieving widgets");
		return ((ResponseEntity<List<WidgetDTO>>) buildResponseEntity("problem_occured_while_retrieving_widgets", locale));
	}

	@ValidRequestMapping(value = "/delete/prop", method = ValidRequestMethodType.DELETE)
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
		return ((ResponseEntity<WidgetProperties>) buildResponseEntity("problem_occured_while_deleting_widget", locale));
	}

	@ValidRequestMapping(value = "/devActive")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Integer> countActiveDevices(@ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				List<Device> devices = deviceUtils.getAllDevicesFromCurrentContext(context, false);
				return new ResponseEntity<>(devices.size(), HttpStatus.OK);
			}
		}
		log.error("Problem occured while retrieving widgets");
		return ((ResponseEntity<Integer>) buildResponseEntity("problem_occured_while_retrieving_widgets", locale));
	}

	/**
	 * In this function we have to define an unique name and description for each necessary widget api call. The implementation is in
	 * WidgetUtils.getValueFromApi function
	 */
	@WidgetFunction(name = StaticConfigItems.WIDGET_API_GROUPS, description = "This function returns the groups for license download")
	@WidgetFunction(name = StaticConfigItems.WIDGET_API_LAST_NOTIFICATIONS, description = "This function returns the last 3 notifications")
	@WidgetFunction(name = StaticConfigItems.WIDGET_API_EXEC_QUERIES, description = "This function returns the count of executed queries")
	@WidgetFunction(name = StaticConfigItems.WIDGET_API_ACTIVE_DEVICES, description = "This function returns the count of the active devices")
	public void defineUniqueWidgetApi() {
	}

}
