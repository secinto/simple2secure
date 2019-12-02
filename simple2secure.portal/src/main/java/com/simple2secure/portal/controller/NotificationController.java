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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Notification;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.NotificationUtils;

import simple2secure.validator.annotation.ValidInput;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputContext;
import simple2secure.validator.model.ValidInputDevice;
import simple2secure.validator.model.ValidInputLocale;

@RestController
@RequestMapping(StaticConfigItems.NOTIFICATION_API)
public class NotificationController {

	static final Logger log = LoggerFactory.getLogger(NotificationController.class);

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private NotificationUtils notificationUtils;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@ValidRequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'DEVICE')")
	public ResponseEntity<Notification> saveNotification(@RequestBody Notification notification, @PathVariable ValidInputDevice deviceId,
			@ValidInput ValidInputLocale locale) {
		if (notification != null && !Strings.isNullOrEmpty(deviceId.getValue())) {
			if (notificationUtils.addNewNotificationPod(notification.getContent(), deviceId.getValue())) {
				return new ResponseEntity<>(notification, HttpStatus.OK);
			}
		}
		log.error("Problem occured while saving notification");
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("error_while_saving_notification", locale.getValue())), HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Notification>> getNotificationsByContextId(@ValidInput ValidInputContext contextId,
			@ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			List<Notification> notifications = notificationRepository.findAllSortDescending(contextId.getValue());
			if (notifications != null) {
				return new ResponseEntity<>(notifications, HttpStatus.OK);
			}
		}
		log.error("Problem occured while retrieving notifications for context id {}", contextId.getValue());
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_notifications", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/read", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Notification> setNotificationRead(@RequestBody Notification notification, @ValidInput ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (notification != null) {
			notification.setRead(true);
			notificationRepository.update(notification);
			return new ResponseEntity<>(notification, HttpStatus.OK);
		}

		log.error("Problem occured while updating read parameter");
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("error_while_saving_notification", locale.getValue())), HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/read")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Integer> getCountOfUnreadNotifications(@ValidInput ValidInputContext contextId, @ValidInput ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			List<Notification> unreadNotifications = notificationRepository.getNotificationByReadValue(contextId.getValue(), false);
			return new ResponseEntity<>(unreadNotifications.size(), HttpStatus.OK);
		}

		log.error("Problem occured while retrieving number of unread notifications");
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_notifications", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}
}
