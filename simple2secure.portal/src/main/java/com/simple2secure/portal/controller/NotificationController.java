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
import com.simple2secure.api.model.Notification;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

	static final Logger log = LoggerFactory.getLogger(NotificationController.class);

	@Autowired
	private NotificationRepository repository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'POD')")
	public ResponseEntity<Notification> saveNotification(@RequestBody Notification notification,
			@RequestHeader("Accept-Language") String locale) {
		if (notification != null) {
			repository.save(notification);
			return new ResponseEntity<Notification>(notification, HttpStatus.OK);
		}
		log.error("Problem occured while saving notification");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_saving_notification", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Notification>> getNotificationsByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {
			// List<Notification> notifications = repository.findByContextId(contextId);
			List<Notification> notifications = repository.findAll();
			if (notifications != null) {
				return new ResponseEntity<List<Notification>>(notifications, HttpStatus.OK);
			}
		}
		log.error("Problem occured while retrieving notifications for context id {}", contextId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_notifications", locale)),
				HttpStatus.NOT_FOUND);
	}
}
