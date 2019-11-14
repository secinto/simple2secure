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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Widget;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.WidgetRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
@RequestMapping("/api/widget")
public class WidgetController {

	@Autowired
	WidgetRepository widgetRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	static final Logger log = LoggerFactory.getLogger(WidgetController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<Widget>> getAllWidgets(@RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(locale)) {
			List<Widget> widgets = widgetRepository.findAll();
			return new ResponseEntity<>(widgets, HttpStatus.OK);
		}
		log.error("Problem occured while retrieving widgets");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_widgets", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/delete/{widgetId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<Widget> deleteWidget(@PathVariable("widgetId") String widgetId, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(widgetId)) {
			Widget widget = widgetRepository.find(widgetId);
			if (widget != null) {
				widgetRepository.delete(widget);
				return new ResponseEntity<>(widget, HttpStatus.OK);
			}
		}
		log.error("Problem occured while deleting widget with id {}", widgetId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_widget", locale)),
				HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<Widget> saveWidget(@RequestBody Widget widget, @RequestHeader("Accept-Language") String locale)
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
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_settings", locale)),
				HttpStatus.NOT_FOUND);
	}
}
